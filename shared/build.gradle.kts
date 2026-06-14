import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.kotlinSerialization)
}


// Version — set chapterstage.version in gradle.properties
val chapterStageVersion: String = (project.findProperty("chapterstage.version") as? String) ?: "1.0.3"

// Build flavor: pass -Pflavor=prod for real API. Default is dev (mock data).
val chapterStageFlavor: String = (project.findProperty("flavor") as? String) ?: "dev"
val isDevFlavor: Boolean = chapterStageFlavor != "prod"

val flavorOutputDir = layout.buildDirectory.dir("generated/source/appflavor")

abstract class GenerateAppFlavorTask : DefaultTask() {
    @get:Input
    abstract val flavor: Property<String>

    @get:Input
    abstract val useMockData: Property<Boolean>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val outFile = outputDir.get().asFile.resolve("com/devscion/chapterstage/AppFlavor.kt")
        outFile.parentFile.mkdirs()
        outFile.writeText(
            """
            |package com.devscion.chapterstage
            |
            |object AppFlavor {
            |    const val FLAVOR: String = "${flavor.get()}"
            |    const val USE_MOCK_DATA: Boolean = ${useMockData.get()}
            |}
            |
            """.trimMargin()
        )
    }
}

val generateAppFlavor by tasks.registering(GenerateAppFlavorTask::class) {
    flavor.set(chapterStageFlavor)
    useMockData.set(isDevFlavor)
    outputDir.set(flavorOutputDir)
}


kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm()
    
    js {
        browser()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    
    androidLibrary {
       namespace = "com.devscion.chapterstage.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_17
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            //Koin
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            // Navigation
            implementation(libs.navigation.compose)
            //Serialization
            implementation(libs.kotlinx.serialization.json)

            // Ktor Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
            implementation(libs.ktor.client.js)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.logback.classic)
        }
    }
}

tasks.matching { it.name.startsWith("compileKotlin") }.configureEach {
    dependsOn(generateAppFlavor)
}

tasks.register("printVersion") {
    doLast { println(chapterStageVersion) }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}