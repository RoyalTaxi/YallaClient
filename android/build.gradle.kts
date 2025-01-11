plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.ildam.technologies.yalla.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "uz.yalla.client"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "0.2"
        resourceConfigurations.plus(listOf("uz", "ru"))
    }
    buildFeatures {
        compose = true
        buildConfig = true
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
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(projects.feature.android.dgis)
    implementation(projects.feature.android.auth)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.animation.android)
    debugImplementation(libs.compose.ui.tooling)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Koin
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.insert.koin.koin.androidx.compose)
    testImplementation(libs.koin.test.junit4)

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

    // Advanced BottomSheet
    implementation(libs.advanced.bottomsheet.material3)

    // ConstraintLayout
    implementation(libs.androidx.constraintlayout.compose)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.paging.common)
    implementation(libs.paging.compose.common)

    // Kotlinx Date time
    implementation(libs.kotlinx.datetime)

    // Google pay
    implementation(libs.play.services.wallet)

    // Lottie compose
    implementation(libs.lottie.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Play update
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

}