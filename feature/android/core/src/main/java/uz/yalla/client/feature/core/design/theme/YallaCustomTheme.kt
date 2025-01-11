package uz.yalla.client.feature.core.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import uz.yalla.client.feature.core.design.color.ColorScheme
import uz.yalla.client.feature.core.design.color.LocalCustomColorScheme
import uz.yalla.client.feature.core.design.font.FontScheme
import uz.yalla.client.feature.core.design.font.LocalCustomTypography

object YallaTheme {
    val color: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalCustomColorScheme.current

    val font: FontScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalCustomTypography.current
}

@Composable
fun YallaCustomTheme(
    color: ColorScheme,
    typography: FontScheme,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalCustomColorScheme provides color,
        LocalCustomTypography provides typography
    ) {
        content()
    }
}