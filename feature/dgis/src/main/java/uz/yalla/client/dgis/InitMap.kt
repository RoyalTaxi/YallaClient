package uz.yalla.client.dgis

import android.content.Context
import ru.dgis.sdk.DGis
import ru.dgis.sdk.Context as GisContext

object InitMap {
    lateinit var context: GisContext

    fun init(
        applicationContext: Context
    ) {
        DGis.initialize(
            appContext = applicationContext,
//            apiKeys = ApiKeys(
//                map = BuildConfig.MAP_API_KEY,
//                directory = ""
//            )
        ).apply { context = this }
    }
}