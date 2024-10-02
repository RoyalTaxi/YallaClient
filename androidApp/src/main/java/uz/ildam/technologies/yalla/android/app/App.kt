package uz.ildam.technologies.yalla.android.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import uz.ildam.technologies.yalla.android.di.Navigation
import uz.ildam.technologies.yalla.core.data.di.Common

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                Navigation.module,
                *Common.modules.toTypedArray()
            )
        }
    }
}