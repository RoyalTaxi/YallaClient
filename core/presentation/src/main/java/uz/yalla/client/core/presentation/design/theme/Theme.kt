package uz.yalla.client.core.presentation.design.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.koin.compose.koinInject
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.type.ThemeType
import uz.yalla.client.core.presentation.design.color.backgroundDay
import uz.yalla.client.core.presentation.design.color.backgroundNight
import uz.yalla.client.core.presentation.design.color.primaryDay
import uz.yalla.client.core.presentation.design.color.primaryNight
import uz.yalla.client.core.presentation.design.color.yallaDark
import uz.yalla.client.core.presentation.design.color.yallaLight
import uz.yalla.client.core.presentation.design.font.fontScheme


private val colorPaletteL = darkColorScheme(
    primary = primaryNight,
    secondary = primaryNight,
    tertiary = primaryNight,
    background = backgroundDay,
    onSurface = Color.Black
)

private val darkColorPaletteL = lightColorScheme(
    primary = primaryDay,
    secondary = primaryDay,
    tertiary = primaryDay,
    background = backgroundNight,
    onSurface = Color.White
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YallaTheme(
    dynamicColor: Boolean = false,
    isInDarkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val prefs = koinInject<AppPreferences>()
    val themeType by prefs.themeType.collectAsState(initial = ThemeType.SYSTEM)
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicDarkColorScheme(context)
        }

        isSystemInDarkTheme() -> darkColorPaletteL
        else -> colorPaletteL
    }

    val rippleConfiguration = RippleConfiguration(
        color = when (themeType) {
            ThemeType.LIGHT -> Color.Black
            ThemeType.DARK -> Color.White
            ThemeType.SYSTEM -> if (isInDarkMode) Color.White else Color.Black
        },
        rippleAlpha = RippleAlpha(
            pressedAlpha = 0.12f,
            focusedAlpha = 0.08f,
            draggedAlpha = 0.12f,
            hoveredAlpha = 0.08f
        )
    )


    val yallaColorScheme = when (themeType) {
        ThemeType.DARK -> yallaDark()
        ThemeType.LIGHT -> yallaLight()
        ThemeType.SYSTEM -> if (isSystemInDarkTheme()) yallaDark() else yallaLight()
    }

    YallaCustomTheme(
        yallaColorScheme,
        fontScheme
    ) {
        CompositionLocalProvider(LocalRippleConfiguration provides rippleConfiguration) {
            MaterialTheme(
                colorScheme = colorScheme,
                content = content
            )
        }
    }
}