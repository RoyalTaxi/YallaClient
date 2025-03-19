plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.payment.data"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.service.payment)
    implementation(projects.feature.payment.domain)
}