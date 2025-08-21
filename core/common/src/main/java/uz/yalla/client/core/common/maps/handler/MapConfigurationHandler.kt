package uz.yalla.client.core.common.maps.handler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.maps.manager.MapIconManager
import uz.yalla.client.core.common.maps.manager.MapThemeManager
import uz.yalla.client.core.domain.model.type.ThemeType

class MapConfigurationHandler(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val themeManager: MapThemeManager,
    private val iconManager: MapIconManager,
    private val onConfigurationChanged: () -> Unit
) {
    private val configurationChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_CONFIGURATION_CHANGED) {
                if (themeManager.currentThemeType == ThemeType.SYSTEM) {
                    coroutineScope.launch {
                        delay(500)
                        themeManager.updateTheme(context, ThemeType.SYSTEM)
                        iconManager.clearCache()
                        iconManager.initializeIcons()
                        onConfigurationChanged()
                    }
                }
            }
        }
    }

    fun registerReceiver() {
        context.registerReceiver(
            configurationChangeReceiver,
            IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        )
    }

    fun unregisterReceiver() {
        try {
            context.unregisterReceiver(configurationChangeReceiver)
        } catch (e: Exception) {
        }
    }
}
