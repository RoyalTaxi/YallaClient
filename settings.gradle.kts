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
        maven { url = uri("https://jitpack.io") }

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

include(":android")
include(":shared")

include(":feature")
include(":feature:android")
include(":feature:android:dgis")
include(":feature:android:auth")
include(":feature:android:core")
