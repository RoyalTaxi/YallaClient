plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    id("kotlin-parcelize")
}

android {
    namespace = "uz.yalla.client.core.domain"
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)

    testApi(projects.core.test)
}