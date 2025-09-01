package uz.yalla.client.core.common.maps.libre.handler

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import uz.yalla.client.core.common.maps.core.handler.MapConfigHandler
import uz.yalla.client.core.common.maps.core.manager.MapIconManager
import uz.yalla.client.core.common.maps.core.manager.MapThemeManager

class LMapConfigHandler(
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
