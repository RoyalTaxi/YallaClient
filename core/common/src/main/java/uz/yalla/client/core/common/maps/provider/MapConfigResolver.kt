package uz.yalla.client.core.common.maps.provider

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import uz.yalla.client.core.common.maps.core.handler.MapConfigHandler
import uz.yalla.client.core.common.maps.core.manager.MapIconManager
import uz.yalla.client.core.common.maps.core.manager.MapThemeManager
import uz.yalla.client.core.common.maps.core.model.MapProvider
import uz.yalla.client.core.common.maps.core.model.toProvider
import uz.yalla.client.core.domain.local.AppPreferences

object MapConfigResolver {
    /**
     * Resolve the proper MapConfigHandler **once** from current AppPreferences.mapType.
     * For Step 1.5 we pass a no-op onConfigurationChanged; we'll wire a redraw later.
     */
    fun resolve(
        context: Context,
        appPreferences: AppPreferences,
        themeManager: MapThemeManager,
        iconManager: MapIconManager,
        onConfigurationChanged: () -> Unit = {}
    ): MapConfigHandler {
        val provider = runBlocking { appPreferences.mapType.first()?.toProvider() ?: MapProvider.GOOGLE }
        val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
        return MapConfigFactory.create(
            provider = provider,
            context = context,
            coroutineScope = scope,
            themeManager = themeManager,
            iconManager = iconManager,
            onConfigurationChanged = onConfigurationChanged
        )
    }
}
