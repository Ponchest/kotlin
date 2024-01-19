// DO NOT EDIT MANUALLY!
// Generated by org/jetbrains/kotlin/generators/arguments/GenerateGradleOptions.kt
// To regenerate run 'generateGradleOptions' task
@file:Suppress("RemoveRedundantQualifierName", "Deprecation", "DuplicatedCode")

package org.jetbrains.kotlin.gradle.dsl

internal abstract class KotlinJsCompilerOptionsDefault @javax.inject.Inject constructor(
    objectFactory: org.gradle.api.model.ObjectFactory
) : org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptionsDefault(objectFactory), org.jetbrains.kotlin.gradle.dsl.KotlinJsCompilerOptions {

    override val friendModulesDisabled: org.gradle.api.provider.Property<kotlin.Boolean> =
        objectFactory.property(kotlin.Boolean::class.java).convention(false)

    override val main: org.gradle.api.provider.Property<org.jetbrains.kotlin.gradle.dsl.JsMainFunctionExecutionMode> =
        objectFactory.property(org.jetbrains.kotlin.gradle.dsl.JsMainFunctionExecutionMode::class.java).convention(org.jetbrains.kotlin.gradle.dsl.JsMainFunctionExecutionMode.CALL)

    override val metaInfo: org.gradle.api.provider.Property<kotlin.Boolean> =
        objectFactory.property(kotlin.Boolean::class.java).convention(true)

    override val moduleKind: org.gradle.api.provider.Property<org.jetbrains.kotlin.gradle.dsl.JsModuleKind> =
        objectFactory.property(org.jetbrains.kotlin.gradle.dsl.JsModuleKind::class.java)

    override val moduleName: org.gradle.api.provider.Property<kotlin.String> =
        objectFactory.property(kotlin.String::class.java)

    override val noStdlib: org.gradle.api.provider.Property<kotlin.Boolean> =
        objectFactory.property(kotlin.Boolean::class.java).convention(true)

    @Deprecated(message = "Only for legacy backend. For IR backend please use task.destinationDirectory and moduleName", level = DeprecationLevel.WARNING)
    override val outputFile: org.gradle.api.provider.Property<kotlin.String> =
        objectFactory.property(kotlin.String::class.java)

    override val sourceMap: org.gradle.api.provider.Property<kotlin.Boolean> =
        objectFactory.property(kotlin.Boolean::class.java).convention(false)

    override val sourceMapEmbedSources: org.gradle.api.provider.Property<org.jetbrains.kotlin.gradle.dsl.JsSourceMapEmbedMode> =
        objectFactory.property(org.jetbrains.kotlin.gradle.dsl.JsSourceMapEmbedMode::class.java)

    override val sourceMapNamesPolicy: org.gradle.api.provider.Property<org.jetbrains.kotlin.gradle.dsl.JsSourceMapNamesPolicy> =
        objectFactory.property(org.jetbrains.kotlin.gradle.dsl.JsSourceMapNamesPolicy::class.java)

    override val sourceMapPrefix: org.gradle.api.provider.Property<kotlin.String> =
        objectFactory.property(kotlin.String::class.java)

    override val target: org.gradle.api.provider.Property<kotlin.String> =
        objectFactory.property(kotlin.String::class.java).convention("es5")

    override val typedArrays: org.gradle.api.provider.Property<kotlin.Boolean> =
        objectFactory.property(kotlin.Boolean::class.java).convention(true)

    override val useEsClasses: org.gradle.api.provider.Property<kotlin.Boolean> =
        objectFactory.property(kotlin.Boolean::class.java)
}
