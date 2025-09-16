package uz.yalla.client.app

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent
import uz.yalla.client.BuildConfig
import uz.yalla.client.core.data.di.Common
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.di.Navigation

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val staticPreferences: StaticPreferences by lazy { KoinJavaComponent.getKoin().get() }

        AndroidThreeTen.init(this)
        MapsInitializer.initialize(this)

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
        startKoin {
            androidContext(this@App)
            printLogger(level = Level.DEBUG)
            modules(
                *Common.modules.toTypedArray(),
                *Navigation.modules.toTypedArray()
            )
        }

        staticPreferences.hasInjectedOrderOnEntry = false
    }
}
