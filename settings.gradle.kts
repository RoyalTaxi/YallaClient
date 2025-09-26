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

rootProject.name = "Yalla"

include(":app")

include(
    ":core:analytics",
    ":core:data",
    ":core:domain",
    ":core:service",
    ":core:presentation",
    ":core:common",
    ":core:dgis",
    ":core:test"
)

include(
    ":feature:auth:data",
    ":feature:auth:domain",
    ":feature:auth:presentation",

    ":feature:contact:presentation",

    ":feature:history:data",
    ":feature:history:domain",
    ":feature:history:presentation",

    ":feature:info:presentation",

    ":feature:intro:presentation",

    ":feature:home:data",
    ":feature:home:domain",
    ":feature:home:presentation",

    ":feature:order:data",
    ":feature:order:domain",
    ":feature:order:presentation",

    ":feature:payment:data",
    ":feature:payment:domain",
    ":feature:payment:presentation",

    ":feature:places:data",
    ":feature:places:domain",
    ":feature:places:presentation",

    ":feature:profile:data",
    ":feature:profile:domain",
    ":feature:profile:presentation",

    ":feature:registration:presentation",

    ":feature:setting:data",
    ":feature:setting:domain",
    ":feature:setting:presentation",

    ":feature:web:presentation",

    ":feature:bonus:presentation",

    ":feature:notification:data",
    ":feature:notification:domain",
    ":feature:notification:presentation",

    ":feature:promocode:data",
    ":feature:promocode:domain",
    ":feature:promocode:presentation"
)

include(
    ":service:auth",
    ":service:home",
    ":service:places",
    ":service:history",
    ":service:order",
    ":service:payment",
    ":service:profile",
    ":service:config",
    ":service:notification",
    ":service:promocode",
    ":benchmark"
)
