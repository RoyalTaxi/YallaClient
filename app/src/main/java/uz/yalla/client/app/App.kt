package uz.yalla.client.app

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import uz.yalla.client.core.data.di.Common
import uz.yalla.client.core.data.enums.MapType
import uz.yalla.client.core.data.local.AppPreferences
//import uz.yalla.client.core.dgis.InitMap
import uz.yalla.client.di.Navigation

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        AppPreferences.init(this)

        AppPreferences.mapType = MapType.Google

        if (AppPreferences.mapType == MapType.Google) MapsInitializer.initialize(this)
//        else InitMap.init(this)

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