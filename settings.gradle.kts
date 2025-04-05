enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    includeBuild("build-logic")
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

include(":feature:auth:data", ":feature:auth:domain", ":feature:auth:presentation")
include(":feature:contact:presentation")
include(":feature:history:data", ":feature:history:domain", ":feature:history:presentation")
include(":feature:info:presentation")
include(":feature:intro:presentation")
include(":feature:map:data", ":feature:map:domain", ":feature:map:presentation")
include(":feature:order:data", ":feature:order:domain", ":feature:order:presentation")
include(":feature:payment:data", ":feature:payment:domain", ":feature:payment:presentation")
include(":feature:places:data", ":feature:places:domain", ":feature:places:presentation")
include(":feature:profile:data", ":feature:profile:domain", ":feature:profile:presentation")
include(":feature:registration:presentation")
include(":feature:setting:data", ":feature:setting:domain", ":feature:setting:presentation")
include(":feature:web:presentation")

include(":service:auth")
include(":service:map")
include(":service:places")
include(":service:histroy")
include(":service:order")
include(":service:payment")
include(":service:profile")
include(":service:setting")
include(":core:test")