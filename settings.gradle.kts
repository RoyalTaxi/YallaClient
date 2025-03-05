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

include(":app")

include(
    ":core:data",
    ":core:domain",
    ":core:service",
    ":core:presentation",
    ":core:common",
    ":core:dgis"
)

include(":feature:dgis")
include(":feature:core")

include(":feature:auth:data", ":feature:auth:domain", ":feature:auth:presentation")
include(":feature:history:data", ":feature:history:domain", ":feature:history:presentation")
include(":feature:setting:data", ":feature:setting:domain")
include(":feature:places:presentation")
include(":feature:map:domain", ":feature:map:presentation")
include(":feature:order:domain")


include(":feature:intro:presentation")
include(":feature:registration:presentation")
include(":feature:payment:presentation")
include(":feature:info:presentation")
include(":feature:setting:presentation")
include(":feature:profile:presentation")
include(":feature:web:presentation")
include(":feature:contact:presentation")

include(":service:auth")
include(":service:map")
include(":service:places")
include(":service:histroy")
include(":service:order")
include(":service:payment")
include(":service:profile")
include(":service:setting")
include(":feature:places:domain")
include(":feature:order:data")
include(":feature:profile:data")
include(":feature:profile:domain")
include(":feature:places:data")
include(":feature:map:data")
include(":feature:payment:data")
include(":feature:payment:domain")
include(":feature:order:presentation")
