plugins {
    kotlin("jvm")
    id("jps-compatible")
}

dependencies {
    api(project(":compiler:cli-common"))
    api(project(":compiler:resolution.common"))
    api(project(":compiler:frontend.java"))
    api(project(":compiler:frontend:cfg"))
    api(project(":compiler:ir.backend.common"))
    api(project(":compiler:backend.jvm"))
    api(project(":compiler:light-classes"))
    api(project(":compiler:javac-wrapper"))
    api(project(":compiler:ir.serialization.jvm"))
    api(project(":js:js.translator"))
    api(project(":native:frontend.native"))
    api(project(":wasm:wasm.frontend"))
    api(project(":kotlin-util-klib"))
    api(project(":kotlin-util-klib-metadata"))

    compileOnly(toolsJarApi())
    compileOnly(intellijCore())
    compileOnly(commonDependency("org.jetbrains.intellij.deps:trove4j"))
    compileOnly(commonDependency("org.jetbrains.intellij.deps:asm-all"))

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)

    testImplementation(intellijCore())
    testImplementation(commonDependency("org.jetbrains.kotlinx", "kotlinx-coroutines-core"))
    testRuntimeOnly(commonDependency("org.jetbrains.intellij.deps.fastutil:intellij-deps-fastutil"))
    testRuntimeOnly(commonDependency("org.codehaus.woodstox:stax2-api"))
    testRuntimeOnly(commonDependency("com.fasterxml:aalto-xml"))
}

sourceSets {
    "main" {
        projectDefault()
    }
    "test" { projectDefault() }
}

allprojects {
    optInToExperimentalCompilerApi()
}

testsJar {}

projectTest(jUnitMode = JUnitMode.JUnit5) {
    workingDir = rootDir
    useJUnitPlatform()
}
