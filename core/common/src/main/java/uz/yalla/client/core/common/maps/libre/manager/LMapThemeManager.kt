package uz.yalla.client.core.common.maps.libre.manager

import android.content.Context
import org.maplibre.android.maps.MapLibreMap
import uz.yalla.client.core.common.maps.core.manager.MapThemeManager

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
        mlMap.setStyle(styleUrl)
    }
}
