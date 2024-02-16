/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.unitTests

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.kotlin.dsl.project
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dependencyResolutionTests.mavenCentralCacheRedirector
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.multiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.internal
import org.jetbrains.kotlin.gradle.plugin.mpp.resources.KotlinTargetResourcesPublication
import org.jetbrains.kotlin.gradle.plugin.mpp.resources.publication.resourcesVariantViewFromCompileDependencyConfiguration
import org.jetbrains.kotlin.gradle.plugin.mpp.resources.resourcesPublicationExtension
import org.jetbrains.kotlin.gradle.util.*
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class KotlinTargetVariantResourcesResolutionTests {

    @Test
    fun `test direct dependency - is the same - for all resolution methods and supported scopes`() {
        resolutionMethods().forEach { resolutionMethod ->
            dependencyScopesWithResources().forEach { dependencyScope ->
                testDirectDependencyOnResourcesProducer(
                    producerTarget = { linuxX64() },
                    consumerTarget = { linuxX64() },
                    dependencyScope = dependencyScope,
                    resolutionMethod = resolutionMethod,
                    expectedResult = { _, producer ->
                        hashSetOf(producer.buildFile("kotlin-multiplatform-resources/zip-for-publication/linuxX64/producer.zip"))
                    },
                )
            }
        }
    }

    @Test
    fun `test direct dependency - between different native targets - with artifact view`() {
        testDirectDependencyOnResourcesProducer(
            producerTarget = { linuxX64() },
            consumerTarget = { linuxArm64() },
            dependencyScope = { ::implementation },
            resolutionMethod = { resolveResourcesWithLenientArtifactView() },
            expectedResult = { _, _ -> emptySet() },
        )
    }

    @Test
    fun `test direct dependency - between different native targets - with resources configuration`() {
        testDirectDependencyOnResourcesProducer(
            producerTarget = { linuxX64() },
            consumerTarget = { linuxArm64() },
            dependencyScope = { ::implementation },
            resolutionMethod = { resolveResourcesWithResourcesConfigurationFiles(lenient = true) },
            expectedResult = { _, _ -> emptySet() },
        )
    }

    @Test
    fun `test direct dependency - for wasmJs and wasmWasi targets - when using artifact view`() {
        listOf<TargetProvider>(
            { wasmJs() },
            { wasmWasi() },
        ).forEach { target ->
            testDirectDependencyOnResourcesProducer(
                producerTarget = { target() },
                consumerTarget = { target() },
                dependencyScope = { ::implementation },
                resolutionMethod = {
                    resolveResourcesWithLenientArtifactView().filterNot {
                        it.path.contains("org/jetbrains/kotlin/kotlin-stdlib")
                    }.toSet()
                },
                expectedResult = { _, producer ->
                    hashSetOf(
                        producer.buildFile(
                            "kotlin-multiplatform-resources/zip-for-publication/${producer.multiplatformExtension.target().name}/producer.zip"
                        ),
                    )
                },
            )
        }
    }

    @Test
    fun `test direct dependency - for wasmJs - when using resources configuration`() {
        testDirectDependencyOnResourcesProducer(
            producerTarget = { wasmJs() },
            consumerTarget = { wasmJs() },
            dependencyScope = { ::implementation },
            resolutionMethod = {
                resolveResourcesWithResourcesConfigurationFiles().filterNot {
                    it.path.contains("org/jetbrains/kotlin/kotlin-stdlib")
                }.toSet()
            },
            expectedResult = { consumer, producer ->
                hashSetOf(
                    producer.buildFile(
                        "kotlin-multiplatform-resources/zip-for-publication/wasmJs/producer.zip"
                    ),
                    // ???
                    consumer.buildFile(
                        "classes/kotlin/wasmJs/main"
                    )
                )
            },
        )
    }

    @Test
    fun `test direct dependency - for wasmWasi - when using resources configuration`() {
        testDirectDependencyOnResourcesProducer(
            producerTarget = { wasmWasi() },
            consumerTarget = { wasmWasi() },
            dependencyScope = { ::implementation },
            resolutionMethod = {
                resolveResourcesWithResourcesConfigurationFiles().filterNot {
                    it.path.contains("org/jetbrains/kotlin/kotlin-stdlib")
                }.toSet()
            },
            expectedResult = { consumer, producer ->
                hashSetOf(
                    producer.buildFile(
                        "kotlin-multiplatform-resources/zip-for-publication/wasmWasi/producer.zip"
                    ),
                    // ???
                    consumer.buildFile(
                        "classes/kotlin/wasmWasi/main"
                    )
                )
            },
        )
    }

    @Test
    fun `test transitive dependency - without resources in middle project - with lenient artifact view`() {
        testTransitiveDependencyOnResourcesProducer(
            resolutionMethod = { resolveResourcesWithLenientArtifactView() },
            expectedResult = { consumer, middle, producer ->
                setOf(
                    producer.buildFile("kotlin-multiplatform-resources/zip-for-publication/linuxX64/producer.zip"),
                    middle.buildFile("classes/kotlin/linuxX64/main/klib/withoutResources.klib"),
                )
            }
        )
    }

    @Test
    fun `test transitive dependency - without resources in middle project - with configuration`() {
        testTransitiveDependencyOnResourcesProducer(
            resolutionMethod = { resolveResourcesWithResourcesConfigurationFiles() },
            expectedResult = { consumer, middle, producer ->
                setOf(
                    producer.buildFile("kotlin-multiplatform-resources/zip-for-publication/linuxX64/producer.zip"),
                    middle.buildFile("classes/kotlin/linuxX64/main/klib/withoutResources.klib"),
                )
            }
        )
    }

    @Test
    fun `test transitive dependency - without resources in middle project - is filterable by classifier`() {
        testTransitiveDependencyOnResourcesProducer(
            resolutionMethod = {
                resolveResourcesWithResourcesConfiguration().resolvedConfiguration.resolvedArtifacts.filter {
                    it.classifier == "kotlin_resources"
                }.map { it.file }.toSet()
            },
            expectedResult = { consumer, middle, producer ->
                setOf(
                    producer.buildFile("kotlin-multiplatform-resources/zip-for-publication/linuxX64/producer.zip"),
                )
            }
        )
    }

//    @Test
//    fun `test dependency from module with resources`

    private fun resolutionMethods(): List<ResolutionMethod> {
        return listOf(
            { resolveResourcesWithResourcesConfigurationFiles() },
            { resolveResourcesWithLenientArtifactView() },
        )
    }

    private fun dependencyScopesWithResources(): List<DependencyScopeProvider> {
        return listOf(
            { this::implementation },
            { this::api },
            { this::compileOnly },
            // { this::runtimeOnly }, ???
        )
    }

    private fun testDirectDependencyOnResourcesProducer(
        producerTarget: TargetProvider,
        consumerTarget: TargetProvider,
        dependencyScope: DependencyScopeProvider,
        resolutionMethod: ResolutionMethod,
        expectedResult: (consumer: Project, producer: Project) -> Set<File>,
    ) {
        val rootProject = buildProject()
        val producer = rootProject.createSubproject("producer") {
            kotlin { producerTarget() }
            enableMppResourcesPublication(true)
        }
        val consumer = rootProject.createSubproject("consumer") {
            kotlin {
                consumerTarget()
                sourceSets.commonMain {
                    dependencies {
                        dependencyScope()(project(":${producer.name}"))
                    }
                }
            }
            enableMppResourcesPublication(false)
        }
        rootProject.evaluate()
        producer.evaluate()
        consumer.evaluate()
        producer.publishFakeResources(producer.multiplatformExtension.producerTarget())
        assertEquals(
            expectedResult(consumer, producer),
            consumer.multiplatformExtension.consumerTarget().resolutionMethod(),
        )
    }

    private fun testTransitiveDependencyOnResourcesProducer(
        resolutionMethod: ResolutionMethod,
        expectedResult: (consumer: Project, middle: Project, producer: Project) -> Set<File>,
    ) {
        val rootProject = buildProject()
        val producer = rootProject.createSubproject("producer") {
            kotlin { linuxX64() }
            enableMppResourcesPublication(true)
        }

        val middle = rootProject.createSubproject("withoutResources") {
            kotlin {
                linuxX64()
                sourceSets.commonMain {
                    dependencies {
                        implementation(dependencies.project(":${producer.name}"))
                    }
                }
            }
            enableMppResourcesPublication(false)
        }

        val consumer = rootProject.createSubproject("consumer") {
            kotlin {
                linuxX64()
                sourceSets.commonMain {
                    dependencies {
                        implementation(dependencies.project(":${middle.name}"))
                    }
                }
                enableMppResourcesPublication(false)
            }
        }

        rootProject.evaluate()
        producer.evaluate()
        middle.evaluate()
        consumer.evaluate()

        producer.publishFakeResources(producer.multiplatformExtension.linuxX64())

        assertEquals(
            expectedResult(consumer, middle, producer),
            consumer.multiplatformExtension.linuxX64().resolutionMethod(),
        )
    }

    private fun Project.buildFile(path: String) = layout.buildDirectory.file(path).get().asFile

    private fun ProjectInternal.createSubproject(
        name: String,
        code: Project.() -> Unit = {},
    ) = buildProjectWithMPPAndStdlib(
        projectBuilder = {
            withParent(this@createSubproject)
            withName(name)
        },
        code = code,
    )

    private fun buildProjectWithMPPAndStdlib(
        projectBuilder: ProjectBuilder.() -> Unit = { },
        code: Project.() -> Unit = {},
    ) = buildProjectWithMPP(
        projectBuilder = projectBuilder
    ) {
        enableDependencyVerification(false)
        enableDefaultStdlibDependency(true)
        repositories.mavenLocal()
        repositories.mavenCentralCacheRedirector()
        code()
    }

    private fun KotlinTarget.resolveResourcesWithLenientArtifactView(): Set<File> = compilations.getByName("main")
        .resourcesVariantViewFromCompileDependencyConfiguration { lenient(true) }.files.toSet()

    private fun KotlinTarget.resolveResourcesWithResourcesConfiguration() =
        compilations.getByName("main").internal.configurations.resourcesConfiguration

    private fun KotlinTarget.resolveResourcesWithResourcesConfigurationFiles(
        lenient: Boolean = false
    ): Set<File> = resolveResourcesWithResourcesConfiguration().incoming.artifactView { it.isLenient = lenient }.files.toSet()

    private fun Project.publishFakeResources(target: KotlinTarget) {
        project.multiplatformExtension.resourcesPublicationExtension?.publishResourcesAsKotlinComponent(
            target,
            resourcePathForSourceSet = {
                KotlinTargetResourcesPublication.ResourceRoot(
                    project.provider { File(it.name) },
                    emptyList(),
                    emptyList(),
                )
            },
            relativeResourcePlacement = project.provider { File("test") },
        )
    }

}

private typealias TargetProvider = KotlinMultiplatformExtension.() -> (KotlinTarget)
private typealias ResolutionMethod = KotlinTarget.() -> Set<File>
private typealias DependencyScopeProvider = KotlinDependencyHandler.() -> ((Any) -> Dependency?)