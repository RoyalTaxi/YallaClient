enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri("http://artifactory.2gis.dev/sdk-maven-release")
            isAllowInsecureProtocol = true
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("http://artifactory.2gis.dev/sdk-maven-release")
            isAllowInsecureProtocol = true
        }
    }
}

rootProject.name = "YallaKMM"
include(":androidApp")
include(":shared")
include(":android2gis")
