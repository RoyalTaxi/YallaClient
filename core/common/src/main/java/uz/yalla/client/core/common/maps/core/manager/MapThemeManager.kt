package uz.yalla.client.core.common.maps.core.manager

import android.content.Context
import android.content.res.Configuration
import uz.yalla.client.core.domain.model.type.ThemeType

abstract class MapThemeManager {
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

    private fun isNightMode(context: Context): Boolean {
        return (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
    }

    abstract fun applyMapStyle(context: Context, map: Any)
}
