import java.util.Properties

plugins {
    id(libs.plugins.buildlogic.get().pluginId)
    alias(libs.plugins.compose.compiler)
}

val localProperties = Properties().apply {
    load(rootProject.file("environment.properties").inputStream())
}

fun getLocalProperty(name: String): String {
    return localProperties.getProperty(name)
        ?: throw RuntimeException("'$name' should be specified in local.properties")
}

android {
    namespace = "uz.yalla.client.core.dgis"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.ui.tooling)

    api(libs.sdk.map)
}