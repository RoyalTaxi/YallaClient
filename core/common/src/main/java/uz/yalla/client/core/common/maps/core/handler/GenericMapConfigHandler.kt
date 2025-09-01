package uz.yalla.client.core.common.maps.core.handler

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import uz.yalla.client.core.common.maps.core.manager.MapIconManager
import uz.yalla.client.core.common.maps.core.manager.MapThemeManager

/**
 * Provider-agnostic configuration handler: just registers system configuration change receiver
 * and updates theme/icon managers accordingly via the base MapConfigHandler logic.
 */
class GenericMapConfigHandler(
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
)

