/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin.mpp.resources.publication

import org.gradle.api.artifacts.ArtifactView
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.bundling.Zip
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.multiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.launch
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.DefaultKotlinUsageContext
import org.jetbrains.kotlin.gradle.plugin.mpp.internal
import org.jetbrains.kotlin.gradle.plugin.mpp.resources.KotlinTargetResourcesPublicationImpl
import org.jetbrains.kotlin.gradle.plugin.mpp.resources.registerAssembleHierarchicalResourcesTask
import org.jetbrains.kotlin.gradle.plugin.mpp.resources.resourcesPublicationExtension
import org.jetbrains.kotlin.gradle.tasks.registerTask
import org.jetbrains.kotlin.gradle.utils.copyAttributesTo

// Use KotlinMultiplatformExtension to make sure this usage context is only creatable in MPP
@Suppress("UnusedReceiverParameter")
internal fun KotlinMultiplatformExtension.setUpResourcesVariant(
    target: AbstractKotlinTarget,
    compilation: KotlinCompilation<*>,
): DefaultKotlinUsageContext = with(target) {
    var targetRegistersResourcesForPublication = false
    val resourcesVariant = DefaultKotlinUsageContext(
        compilation = compilation,
        dependencyConfigurationName = resourcesElementsConfigurationName,
        // FIXME: Not sure what is the proper output here
        includeIntoProjectStructureMetadata = false,
        publishOnlyIf = {
            targetRegistersResourcesForPublication
        }
    )

    project.multiplatformExtension.resourcesPublicationExtension?.subscribeOnPublishResources(this) { resources ->
        // FIXME: Is this code single-threaded?
        targetRegistersResourcesForPublication = true
        project.launch {
            val copyTask = compilation.registerAssembleHierarchicalResourcesTask(
                targetName,
                resources,
            )
            val zippedResourcesDirectory = project.layout.buildDirectory.dir(
                "${KotlinTargetResourcesPublicationImpl.MULTIPLATFORM_RESOURCES_DIRECTORY}/zip-for-publication/${targetName}"
            )
            val zipResourcesForPublication = project.registerTask<Zip>(
                "${targetName}ZipMultiplatformResourcesForPublication"
            ) { copy ->
                copy.destinationDirectory.set(zippedResourcesDirectory)
                copy.duplicatesStrategy = DuplicatesStrategy.FAIL
            }
            zipResourcesForPublication.configure {
                it.from(copyTask)
            }

            project.artifacts.add(
                compilation.target.internal.resourcesElementsConfigurationName,
                zipResourcesForPublication
            ) { artifact ->
                artifact.extension = "zip"
                artifact.type = "zip"
                artifact.classifier = "kotlin_resources"
            }
        }
    }

    return resourcesVariant
}

// In Gradle 7.2 project dependencies fail to resolve
// FIXME: Use this as an alternative resolution strategy?
internal fun KotlinCompilation<*>.resourcesVariantViewFromCompileDependencyConfiguration(
    configure: ArtifactView.ViewConfiguration.() -> (Unit)
): ArtifactView {
    return internal.configurations.compileDependencyConfiguration.incoming.artifactView {
        it.withVariantReselection()
        it.attributes { viewAttributes ->
            // Lifecycle await?
            project.configurations.getByName(
                target.internal.resourcesElementsConfigurationName
            ).copyAttributesTo(
                project,
                viewAttributes,
            )
        }
        it.configure()
    }
}