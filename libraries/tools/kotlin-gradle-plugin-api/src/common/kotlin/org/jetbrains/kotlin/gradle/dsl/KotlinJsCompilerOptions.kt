// DO NOT EDIT MANUALLY!
// Generated by org/jetbrains/kotlin/generators/arguments/GenerateGradleOptions.kt
// To regenerate run 'generateGradleOptions' task
@file:Suppress("RemoveRedundantQualifierName", "Deprecation", "DuplicatedCode")

package org.jetbrains.kotlin.gradle.dsl

/**
 * Compiler options for Kotlin/JS.
 */
interface KotlinJsCompilerOptions : org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions {

    /**
     * Disable internal declaration export.
     *
     * Default value: false
     */
    @get:org.gradle.api.tasks.Input
    val friendModulesDisabled: org.gradle.api.provider.Property<kotlin.Boolean>

    /**
     * Specify whether the 'main' function should be called upon execution.
     *
     * Possible values: "call", "noCall"
     *
     * Default value: JsMainFunctionExecutionMode.CALL
     */
    @get:org.gradle.api.tasks.Input
    val main: org.gradle.api.provider.Property<org.jetbrains.kotlin.gradle.dsl.JsMainFunctionExecutionMode>

    /**
     * Generate .meta.js and .kjsm files with metadata. Use this to create a library.
     *
     * Default value: true
     */
    @get:org.gradle.api.tasks.Input
    val metaInfo: org.gradle.api.provider.Property<kotlin.Boolean>

    /**
     * The kind of JS module generated by the compiler.
     *
     * Possible values: "plain", "amd", "commonjs", "umd"
     *
     * Default value: JsModuleKind.MODULE_PLAIN
     */
    @get:org.gradle.api.tasks.Input
    val moduleKind: org.gradle.api.provider.Property<org.jetbrains.kotlin.gradle.dsl.JsModuleKind>

    /**
     * Base name of generated files.
     *
     * Default value: null
     */
    @get:org.gradle.api.tasks.Optional
    @get:org.gradle.api.tasks.Input
    val moduleName: org.gradle.api.provider.Property<kotlin.String>

    /**
     * Don't automatically include the default Kotlin/JS stdlib in compilation dependencies.
     *
     * Default value: true
     */
    @get:org.gradle.api.tasks.Input
    val noStdlib: org.gradle.api.provider.Property<kotlin.Boolean>

    /**
     * Destination *.js file for the compilation result.
     *
     * Default value: null
     */
    @Deprecated(message = "Only for legacy backend. For IR backend please use task.destinationDirectory and moduleName", level = DeprecationLevel.WARNING)
    @get:org.gradle.api.tasks.Internal
    val outputFile: org.gradle.api.provider.Property<kotlin.String>

    /**
     * Generate a source map.
     *
     * Default value: false
     */
    @get:org.gradle.api.tasks.Input
    val sourceMap: org.gradle.api.provider.Property<kotlin.Boolean>

    /**
     * Embed source files into the source map.
     *
     * Possible values: "never", "always", "inlining"
     *
     * Default value: null
     */
    @get:org.gradle.api.tasks.Optional
    @get:org.gradle.api.tasks.Input
    val sourceMapEmbedSources: org.gradle.api.provider.Property<org.jetbrains.kotlin.gradle.dsl.JsSourceMapEmbedMode>

    /**
     * Mode for mapping generated names to original names (IR backend only).
     *
     * Possible values: "no", "simple-names", "fully-qualified-names"
     *
     * Default value: null
     */
    @get:org.gradle.api.tasks.Optional
    @get:org.gradle.api.tasks.Input
    val sourceMapNamesPolicy: org.gradle.api.provider.Property<org.jetbrains.kotlin.gradle.dsl.JsSourceMapNamesPolicy>

    /**
     * Add the specified prefix to the paths in the source map.
     *
     * Default value: null
     */
    @get:org.gradle.api.tasks.Optional
    @get:org.gradle.api.tasks.Input
    val sourceMapPrefix: org.gradle.api.provider.Property<kotlin.String>

    /**
     * Generate JS files for the specified ECMA version.
     *
     * Possible values: "es5", "es2015"
     *
     * Default value: "es5"
     */
    @get:org.gradle.api.tasks.Input
    val target: org.gradle.api.provider.Property<kotlin.String>

    /**
     * Translate primitive arrays into JS typed arrays.
     *
     * Default value: true
     */
    @get:org.gradle.api.tasks.Input
    val typedArrays: org.gradle.api.provider.Property<kotlin.Boolean>

    /**
     * Let generated JavaScript code use ES2015 classes.
     *
     * Default value: false
     */
    @get:org.gradle.api.tasks.Input
    val useEsClasses: org.gradle.api.provider.Property<kotlin.Boolean>
}
