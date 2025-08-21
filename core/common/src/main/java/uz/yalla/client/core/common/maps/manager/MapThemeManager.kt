package uz.yalla.client.core.common.maps.manager

import android.content.Context
import android.content.res.Configuration
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import uz.yalla.client.core.common.R
import uz.yalla.client.core.domain.model.type.ThemeType

class MapThemeManager {
    var isDarkTheme: Boolean = false
        private set
    
    var currentThemeType: ThemeType? = null
        private set

    fun updateTheme(context: Context, themeType: ThemeType) {
        currentThemeType = themeType

        isDarkTheme = when (themeType) {
            ThemeType.DARK -> true
            ThemeType.LIGHT -> false
            ThemeType.SYSTEM -> isNightMode(context)
        }
    }

    fun applyMapStyle(context: Context, googleMap: GoogleMap) {
        if (isDarkTheme) {
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.google_dark_map
                )
            )
        } else {
            googleMap.setMapStyle(null)
        }
    }

    private fun isNightMode(context: Context): Boolean {
        return (context.resources.configuration.uiMode and 
                Configuration.UI_MODE_NIGHT_MASK) == 
                Configuration.UI_MODE_NIGHT_YES
    }
}