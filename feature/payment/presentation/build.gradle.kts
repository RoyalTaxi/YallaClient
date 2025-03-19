plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.feature.android.payment"
}

dependencies {

    implementation(projects.core.common)
    implementation(projects.feature.payment.data)
    implementation(projects.feature.payment.domain)
}