plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.core.common"
}

dependencies {
    api(projects.core.data)
    api(projects.core.domain)
    api(projects.core.analytics)
    api(projects.core.presentation)

    implementation(projects.feature.places.domain)
    implementation(projects.feature.home.domain)
    implementation(projects.feature.order.domain)

    implementation(libs.threetenabp)
    implementation(libs.snapper)
    implementation(libs.kotlinx.datetime)

    implementation(libs.maps.compose)
    implementation(libs.play.services.location)

    api(libs.orbit.core)
    api(libs.orbit.viewmodel)
    api(libs.orbit.compose)
    testApi(libs.orbit.test)

    api(libs.maps.compose)
    api(libs.play.services.location)

    implementation(libs.maplibre.compose)
    implementation(libs.lottie.compose)

    implementation(libs.androidx.constraintlayout.compose)

    testApi(projects.core.test)
    androidTestApi(projects.core.test)
}
