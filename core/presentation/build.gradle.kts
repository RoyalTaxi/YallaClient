plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.core.presentation"
}

dependencies {
    implementation(platform(libs.koin.bom))

    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.material)
    testApi(libs.junit)
    androidTestApi(libs.androidx.junit)
    androidTestApi(libs.androidx.espresso.core)

    api(libs.compose.ui)
    api(libs.compose.ui.tooling.preview)
    api(libs.compose.material3)
    api(libs.androidx.activity.compose)
    api(libs.androidx.animation.android)
    debugApi(libs.compose.ui.tooling)

    api(libs.androidx.navigation.compose)

    api(project.dependencies.platform(libs.koin.bom))
    api(libs.koin.core)
    api(libs.koin.android)
    api(libs.insert.koin.koin.androidx.compose)
}