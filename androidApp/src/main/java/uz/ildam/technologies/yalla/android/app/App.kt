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
import uz.ildam.technologies.yalla.core.data.enums.MapType
import uz.ildam.technologies.yalla.core.data.local.AppPreferences

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        AppPreferences.init(this)

        MapsInitializer.initialize(this)

        AppPreferences.accessToken =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI1IiwianRpIjoiNWJlMWU5ZTQ0MDgxOTkzODJjNWE5NTZlYjUyYTIxMWU0YjMzMDE1YmIxYmY5ZDk4YmE3MmYxZGExMjI1ZGU5NmRmNjc2ZmEwNmUyOWJlYzUiLCJpYXQiOjE3MzE2MDk3NTkuODU1NDM1LCJuYmYiOjE3MzE2MDk3NTkuODU1NDM3LCJleHAiOjE3NjMxNDU3NTkuODQ5Nzk0LCJzdWIiOiIxNDk0MzgiLCJzY29wZXMiOlsiY2xpZW50Il19.GANUCtVN2ljpC901V0F7lqkarEAZ4ActXGU9bGplIzXKbBwXzdLIxvqd63tv-82ui5Q_TVNgib324z2ndAbl2p6RuuC-x-mDhSfOHaOa4JNXVVL3teRh8pMBP-O9KmI7HVkB1wKgFqCcsMWzd1bwPwcA5l4w5NrSxrSkUF4CfWJ22iBV8WDr9j_0WDYdeC8gIdZQZXPnRpNUUgq-HBpB19-UzPDpCAZJ3lycb_prbE1P8iAmEWCZAcSlCkMbF-4WTaEYyQgAUqXay6tFNhnpjr24Oo0ZVuiDtys5ZiVCEC73gEG0SYYXI9zdAV5p0WZZfnF1xHzqY0o1IBZUnq75EtaTh7SEPey7YGOGZerfllH4uOY1myPmrMi1DFnyWGAXZ2X1AKSZ65BLUQ-rvgxA6xR5UHzKQimVk49F-5iKO9hL8pY-1152IZP7N2BMvUWdEsDaQT8Is8JRtfGWL5ecrX2D--4Lf-oDY_ftiBKuQHh-KUeLcb8nVyirApQfYmOZiqpb2EsDT9z2P85gUyKnm6mIfwAM13IxuY_2ygXmoLdrTAdosMNhD3586lL43lB_5svNcSMQ4I9a2uTrEMq--2CzQBGgGq5FzrnA5T78rLMKd6h7qz1zD9pyj44qqCyctnc4tp9Ns9rUp-pgDT_or6kPnpObIPWcusQw6CeAw60"
        AppPreferences.mapType = MapType.Google

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