plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.auth.data"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.service.auth)
    implementation(projects.feature.auth.domain)

    testImplementation(projects.core.test)
    androidTestImplementation(projects.core.test)
}