plugins {
    `kotlin-dsl`
}

group = "uz.yalla.client.buildlogic"

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    compileOnly(libs.gradle)
    compileOnly(libs.kotlin.gradle.plugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("service") {
            id = libs.plugins.buildlogic.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}