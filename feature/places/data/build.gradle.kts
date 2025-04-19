plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.places.data"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.service.places)
    implementation(projects.feature.places.domain)

    testImplementation(projects.core.test)
}