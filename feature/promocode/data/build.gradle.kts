plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.promocode.data"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.service.promocode)
    implementation(projects.feature.promocode.domain)

    testImplementation(projects.core.test)
    androidTestImplementation(projects.core.test)
}
