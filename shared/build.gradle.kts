plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version libs.versions.kotlin.get()
}

kotlin {
    android()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val ktor_version = "1.5.4"
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                // SQLDelight
                implementation(libs.sqldelight.coroutines)
                implementation(libs.kotlinx.serialization.core)

                // Ktor
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.server.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                implementation("io.github.aakira:napier:2.7.1")
            }
        }
        val androidMain by getting {
            dependencies {
                // SQL
                api(libs.sqldelight.driver.android)

                //ktor
                implementation(libs.ktor.client.android)
                implementation(libs.ktor.client.okhttp)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.sqldelight.driver.native)
                implementation(libs.ktor.client.ios)
            }
        }

    }
}

sqldelight {
    databases {
        create("LogDb") {
            packageName.set("com.vikas.kmm.db")
        }
    }
}

android {
    namespace = "com.vikas.kmm"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}