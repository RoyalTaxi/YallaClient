package uz.yalla.client.core.common.maps.google.manager

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import uz.yalla.client.core.common.maps.core.manager.MapThemeManager
import uz.yalla.client.core.common.R

class GMapThemeManager : MapThemeManager() {
    override fun applyMapStyle(context: Context, map: Any) {
        val gMap = map as? GoogleMap ?: return
        val styleRes = if (isDarkTheme) R.raw.google_dark_map else null
        gMap.setMapStyle(
            styleRes?.let {
                MapStyleOptions.loadRawResourceStyle(context, it)
            }
        )
    }
}
