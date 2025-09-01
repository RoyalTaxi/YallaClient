package uz.yalla.client.core.common.maps.google.handler

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import uz.yalla.client.core.common.maps.core.handler.MapConfigHandler
import uz.yalla.client.core.common.maps.core.manager.MapIconManager
import uz.yalla.client.core.common.maps.core.manager.MapThemeManager
import uz.yalla.client.core.common.maps.google.manager.GMapIconManager
import uz.yalla.client.core.common.maps.google.manager.GMapThemeManager

class GMapConfigHandler(
    context: Context,
    coroutineScope: CoroutineScope,
    themeManager: MapThemeManager,
    iconManager: MapIconManager,
    onConfigurationChanged: () -> Unit
) : MapConfigHandler(
    context = context,
    coroutineScope = coroutineScope,
    themeManager = themeManager,
    iconManager = iconManager,
    onConfigurationChanged = onConfigurationChanged
) {
}
