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
        resourceConfigurations.plus(listOf("uz", "ru"))
    }
    buildFeatures {
        compose = true
    }
    bundle {
        language {
            enableSplit = false
        }
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
    implementation(libs.koin.android)
    implementation(libs.insert.koin.koin.androidx.compose)

    // Voyager
    implementation(libs.voyager.navigator)
    implementation(libs.voyager.screenmodel)
    implementation(libs.voyager.bottom.sheet.navigator)
    implementation(libs.voyager.tab.navigator)
    implementation(libs.voyager.transitions)
    implementation(libs.voyager.koin)

    // DatePicker
    implementation(libs.snapper)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // LocalDate
    implementation(libs.threetenabp)

    // Location
    implementation(libs.play.services.location)

    // Inspektify
    implementation(libs.inspektify.ktor3)

    // Ktor
    implementation(libs.ktor.client.android)

    // Maps
    implementation(libs.maps.compose)
}