import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

fun getLocalProperty(name: String): String {
    return localProperties.getProperty(name)
        ?: throw RuntimeException("'$name' should be specified in local.properties")
}

android {
    namespace = "uz.ildam.technologies.yalla.android2gis"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        buildConfigField("String", "MAP_API_KEY", getLocalProperty("dgisMapApiKey"))
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

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

    // Public API for downstream modules

    api(libs.sdk.map)
}