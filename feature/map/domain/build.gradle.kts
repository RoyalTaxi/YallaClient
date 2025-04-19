plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.map.domain"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.feature.places.domain)

    testImplementation(projects.core.test)
}