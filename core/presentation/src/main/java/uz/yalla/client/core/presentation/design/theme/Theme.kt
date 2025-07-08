package uz.yalla.client.core.presentation.design.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import org.koin.compose.koinInject
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.type.ThemeType
import uz.yalla.client.core.presentation.design.color.onBlackDay
import uz.yalla.client.core.presentation.design.color.onBlackNight
import uz.yalla.client.core.presentation.design.color.yallaDark
import uz.yalla.client.core.presentation.design.color.yallaLight
import uz.yalla.client.core.presentation.design.font.fontScheme


private val colorPaletteL = lightColorScheme(
    primary = onBlackDay,
    secondary = onBlackDay,
    tertiary = onBlackDay,
    background = onBlackDay
)

private val darkColorPaletteL = darkColorScheme(
    primary = onBlackNight,
    secondary = onBlackNight,
    tertiary = onBlackNight,
    background = onBlackNight
)


@Composable
fun YallaTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicDarkColorScheme(context)
        }

        else -> colorPaletteL

    }

    val prefs = koinInject<AppPreferences>()
    val themeType by prefs.themeType.collectAsState(initial = ThemeType.SYSTEM)

    val yallaColorScheme = when (themeType) {
        ThemeType.DARK -> yallaDark()
        ThemeType.LIGHT -> yallaLight()
        ThemeType.SYSTEM -> if (isSystemInDarkTheme()) yallaDark() else yallaLight()
    }

    YallaCustomTheme(
        yallaColorScheme,
        fontScheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}