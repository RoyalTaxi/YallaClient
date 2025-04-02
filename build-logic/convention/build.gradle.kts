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

gradlePlugin {
    plugins {
        register("service") {
            id = libs.plugins.buildlogic.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}