plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.core.data"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(projects.core.domain)
    api(projects.core.service)
    api(libs.kotlinx.serialization.json)

    api(libs.ktor.client.core)
    api(libs.ktor.client.android)
    api(libs.kotlinx.coroutines.core)
    api(libs.ktor.client.content.negotiation)
    api(libs.ktor.serialization.kotlinx.json)
    api(libs.ktor.client.logging)

    api(project.dependencies.platform(libs.koin.bom))
    api(libs.koin.core)
    api(libs.koin.android)

    implementation(libs.datastore)
    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.inspektify.ktor3)

    testApi(projects.core.test)
}