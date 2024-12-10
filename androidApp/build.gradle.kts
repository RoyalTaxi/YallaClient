import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
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
    namespace = "uz.ildam.technologies.yalla.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "uz.ildam.technologies.yalla.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        resourceConfigurations.plus(listOf("uz", "ru"))
        buildConfigField("String", "MAP_API_KEY", getLocalProperty("dgisMapApiKey"))

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
            isMinifyEnabled = false
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
    implementation(project(":android2gis"))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
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

//    implementation(libs.sdk.map)

}