package uz.ildam.technologies.yalla.android.app

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import ru.dgis.sdk.ApiKeys
import ru.dgis.sdk.DGis
import uz.ildam.technologies.yalla.android.BuildConfig
import uz.ildam.technologies.yalla.android.di.Navigation
import uz.ildam.technologies.yalla.core.data.di.Common
import uz.ildam.technologies.yalla.core.data.local.AppPreferences

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        AppPreferences.init(this)

        MapsInitializer.initialize(this)

        DGis.initialize(
            appContext = this,
            apiKeys = ApiKeys(
                map = BuildConfig.MAP_API_KEY,
                directory = ""
            )
        )

        startKoin {
            androidContext(this@App)
            printLogger(level = org.koin.core.logger.Level.DEBUG)
            modules(
                *Common.modules.toTypedArray(),
                *Navigation.modules.toTypedArray(),
            )
        }
    }
}