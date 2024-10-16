package uz.ildam.technologies.yalla.android.app

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import sp.bvantur.inspektify.ktor.InspektifyKtor
import uz.ildam.technologies.yalla.android.di.Navigation
import uz.ildam.technologies.yalla.core.data.di.Common
import uz.ildam.technologies.yalla.core.data.local.AppPreferences

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        InspektifyKtor.startInspektify()
        AndroidThreeTen.init(this)
        AppPreferences.init(this)
        startKoin {
            androidContext(this@App)
            modules(
                *Navigation.modules.toTypedArray(),
                *Common.modules.toTypedArray()
            )
        }
    }
}