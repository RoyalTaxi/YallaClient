package uz.yalla.client.core.common.maps.provider

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import uz.yalla.client.core.common.maps.core.handler.MapConfigHandler
import uz.yalla.client.core.common.maps.core.manager.MapIconManager
import uz.yalla.client.core.common.maps.core.manager.MapThemeManager
import uz.yalla.client.core.common.maps.core.model.MapProvider
import uz.yalla.client.core.common.maps.google.handler.GMapConfigHandler
import uz.yalla.client.core.common.maps.libre.handler.LMapConfigHandler

object MapConfigFactory {
    fun create(
        provider: MapProvider,
        context: Context,
        coroutineScope: CoroutineScope,
        themeManager: MapThemeManager,
        iconManager: MapIconManager,
        onConfigurationChanged: () -> Unit
    ): MapConfigHandler {
        return when (provider) {
            MapProvider.GOOGLE -> GMapConfigHandler(
                context, coroutineScope, themeManager, iconManager, onConfigurationChanged
            )
            MapProvider.LIBRE -> LMapConfigHandler(
                context, coroutineScope, themeManager, iconManager, onConfigurationChanged
            )
        }
    }
}