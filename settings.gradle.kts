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
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/v6.4.2-SNAPSHOT")
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
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/v6.4.2-SNAPSHOT")

        }
    }
}

rootProject.name = "YallaClient"
include(":androidApp")
include(":shared")
include(":android2gis")
