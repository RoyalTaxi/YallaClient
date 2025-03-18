plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.core.common"
}

dependencies {
    implementation(projects.core.dgis)
    api(projects.core.data)
    api(projects.core.domain)
    api(projects.core.presentation)

    implementation(projects.feature.places.domain)
    implementation(projects.feature.map.domain)
    implementation(projects.feature.order.domain)

    implementation(libs.threetenabp)
    implementation(libs.snapper)
    implementation(libs.kotlinx.datetime)

    implementation(libs.maps.compose)
    implementation(libs.play.services.location)

    implementation(libs.lottie.compose)

    implementation(libs.androidx.constraintlayout.compose)
}