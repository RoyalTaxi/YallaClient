plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.notification.domain"
}

dependencies {

    implementation(projects.core.domain)
    implementation(libs.paging.common)
}