package uz.yalla.client.app

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent
import uz.yalla.client.BuildConfig
import uz.yalla.client.config.Config
import uz.yalla.client.core.analytics.event.Logger
import uz.yalla.client.core.data.di.Common
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.di.Navigation

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Logger.init(this)
        AndroidThreeTen.init(this)
        MapsInitializer.initialize(this)
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 60L }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf(Config.EventCollection.key to false))
        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.setAnalyticsCollectionEnabled(false)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val enabled = remoteConfig.getBoolean(Config.EventCollection.key)
                firebaseAnalytics.setAnalyticsCollectionEnabled(enabled)
            } else {
                firebaseAnalytics.setAnalyticsCollectionEnabled(false)
            }
        }

        remoteConfig.addOnConfigUpdateListener(
            object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    val enabled = remoteConfig.getBoolean(Config.EventCollection.key)
                    firebaseAnalytics.setAnalyticsCollectionEnabled(enabled)
                }

                override fun onError(error: FirebaseRemoteConfigException) {
                    /* no-op */
                }
            }
        )

        startKoin {
            androidContext(this@App)
            printLogger(level = Level.DEBUG)
            modules(
                *Common.modules.toTypedArray(),
                *Navigation.modules.toTypedArray()
            )
        }

        val staticPreferences: StaticPreferences by lazy { KoinJavaComponent.getKoin().get() }
        staticPreferences.hasInjectedOrderOnEntry = false
    }
}
