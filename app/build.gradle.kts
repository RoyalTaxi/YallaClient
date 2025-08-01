import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "uz.yalla.client"
    compileSdk = 35
    defaultConfig {
        applicationId = "uz.yalla.client"
        minSdk = 26
        targetSdk = 35
        versionCode = 73
        versionName = "0.0.4"
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
//        getByName("debug") {
//            isMinifyEnabled = true
//            isShrinkResources = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
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

    buildFeatures {
        viewBinding = true
    }

    applicationVariants.configureEach {
        outputs.configureEach {
            (this as? ApkVariantOutputImpl)?.outputFileName = "${rootProject.name} $versionName.apk"
        }

        tasks.named(
            "sign${flavorName.uppercaseFirstChar()}${buildType.name.uppercaseFirstChar()}Bundle",
            com.android.build.gradle.internal.tasks.FinalizeBundleTask::class.java
        ) {
            val file = finalBundleFile.asFile.get()
            val finalFile = File(file.parentFile, "${rootProject.name} $versionName.aab")
            finalBundleFile.set(finalFile)
        }
    }
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.presentation)

    implementation(projects.feature.setting.domain)
    implementation(projects.feature.auth.presentation)
    implementation(projects.feature.intro.presentation)
    implementation(projects.feature.registration.presentation)
    implementation(projects.feature.payment.presentation)
    implementation(projects.feature.places.presentation)
    implementation(projects.feature.profile.presentation)
    implementation(projects.feature.history.presentation)
    implementation(projects.feature.info.presentation)
    implementation(projects.feature.setting.presentation)
    implementation(projects.feature.web.presentation)
    implementation(projects.feature.contact.presentation)
    implementation(projects.feature.map.presentation)
    implementation(projects.feature.order.presentation)
    implementation(projects.feature.notification.presentation)
    implementation(projects.feature.bonus.presentation)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.animation.android)
    implementation(libs.androidx.runtime.tracing)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.play.services.auth.api.phone)
    implementation(libs.androidx.appcompat)
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

    // Google Maps
    implementation(libs.play.services.maps)

    // Lottie compose
    implementation(libs.lottie.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Play update
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.messaging.ktx)
}

configurations.all {
    exclude(group = "org.jogamp.gluegen", module = "gluegen-rt")
    exclude(group = "org.jogamp.jogl", module = "jogl-all")
    exclude(group = "dev.datlag", module = "jcef")
    exclude(group = "dev.datlag", module = "kcef")
}