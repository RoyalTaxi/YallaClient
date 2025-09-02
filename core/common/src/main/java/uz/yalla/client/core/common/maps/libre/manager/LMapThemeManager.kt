package uz.yalla.client.core.common.maps.libre.manager

import android.content.Context
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.maps.MapLibreMap
import uz.yalla.client.core.common.maps.core.manager.MapThemeManager
import uz.yalla.client.core.common.utils.hasLocationPermission
import uz.yalla.client.core.common.maps.core.util.MapPaddingStore

class LMapThemeManager : MapThemeManager {
    private val dayStyleUrl: String
    private val nightStyleUrl: String

    constructor(dayStyleUrl: String, nightStyleUrl: String) : super() {
        this.dayStyleUrl = dayStyleUrl
        this.nightStyleUrl = nightStyleUrl
    }

    override fun applyMapStyle(context: Context, map: Any) {
        val mlMap = map as? MapLibreMap ?: return
        val styleUrl = if (isDarkTheme) nightStyleUrl else dayStyleUrl
        mlMap.setStyle(styleUrl) { style ->
            if (!context.hasLocationPermission()) return@setStyle
            try {
                val options = LocationComponentActivationOptions
                    .builder(context, style)
                    .build()
                val locationComponent = mlMap.locationComponent
                locationComponent.activateLocationComponent(options)
                locationComponent.isLocationComponentEnabled = true
                locationComponent.cameraMode = CameraMode.TRACKING
            } catch (_: Throwable) {
                // Silently ignore if the SDK version lacks LocationComponent or any issue occurs
            }

            // Reapply last-known map padding because setStyle resets it in MapLibre
            try {
                mlMap.setPadding(
                    MapPaddingStore.left,
                    MapPaddingStore.top,
                    MapPaddingStore.right,
                    MapPaddingStore.bottom
                )
            } catch (_: Throwable) { /* ignore */ }
        }
    }
}
