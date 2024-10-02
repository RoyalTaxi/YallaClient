package uz.ildam.technologies.yalla.android.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import uz.ildam.technologies.yalla.android.design.color.ColorScheme
import uz.ildam.technologies.yalla.android.design.color.LocalCustomColorScheme
import uz.ildam.technologies.yalla.android.design.font.FontScheme
import uz.ildam.technologies.yalla.android.design.font.LocalCustomTypography

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