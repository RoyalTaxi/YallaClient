package uz.yalla.client.core.common.maps.core.manager

import android.content.Context
import uz.yalla.client.core.common.maps.google.manager.GMapThemeManager
import uz.yalla.client.core.common.maps.libre.manager.LMapThemeManager

class DelegatingMapThemeManager(
    private val gTheme: GMapThemeManager,
    private val lTheme: LMapThemeManager
) : MapThemeManager() {
    override fun applyMapStyle(context: Context, map: Any) {
        currentThemeType?.let { themeType ->
            gTheme.updateTheme(context, themeType)
            lTheme.updateTheme(context, themeType)
        }
        when (map) {
            is com.google.android.gms.maps.GoogleMap -> gTheme.applyMapStyle(context, map)
            is org.maplibre.android.maps.MapLibreMap -> lTheme.applyMapStyle(context, map)
        }
    }
}
