plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.ildam.technologies.yalla.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "uz.ildam.technologies.yalla.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.ui.tooling)

    // Koin
    implementation(libs.koin.android) // Koin Android
    implementation(libs.insert.koin.koin.androidx.compose) // Koin-Compose

    // Voyager
    implementation(libs.voyager.navigator) // Navigator
    implementation(libs.voyager.screenmodel) // Screen Model
    implementation(libs.voyager.bottom.sheet.navigator) // BottomSheetNavigator
    implementation(libs.voyager.tab.navigator) // TabNavigator
    implementation(libs.voyager.transitions) // Transitions
    implementation(libs.voyager.koin) // Koin
}