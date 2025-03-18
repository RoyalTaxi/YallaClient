plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.core.service"
}

dependencies {
    api(projects.core.domain)
    api(libs.ktor.client.core)
    api(libs.kotlinx.coroutines.core)
    api(libs.ktor.client.content.negotiation)
    api(libs.ktor.serialization.kotlinx.json)
    api(libs.ktor.client.logging)

    api(project.dependencies.platform(libs.koin.bom))
    api(libs.koin.core)
}