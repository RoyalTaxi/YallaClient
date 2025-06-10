plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.setting.data"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.service.config)
    implementation(projects.feature.setting.domain)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging.ktx)

    testImplementation(projects.core.test)
    androidTestImplementation(projects.core.test)
}