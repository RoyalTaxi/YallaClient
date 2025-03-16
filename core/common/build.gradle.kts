import org.jetbrains.kotlin.utils.addToStdlib.safeAs

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "uz.yalla.client.core.common"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    }
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.dgis)
    implementation(projects.core.domain)
    implementation(projects.core.presentation)

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