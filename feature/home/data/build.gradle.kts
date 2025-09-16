plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.home.data"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.service.home)
    implementation(projects.feature.home.domain)
    implementation(projects.feature.places.domain)

    testImplementation(projects.core.test)
}