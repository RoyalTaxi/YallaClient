plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.payment.domain"
}

dependencies {
    implementation(projects.core.domain)
}