package uz.ildam.technologies.yalla.android.design.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import uz.ildam.technologies.yalla.android.design.color.YallaBlack
import uz.ildam.technologies.yalla.android.design.color.YallaGray
import uz.ildam.technologies.yalla.android.design.color.YallaGray2
import uz.ildam.technologies.yalla.android.design.color.yallaLight
import uz.ildam.technologies.yalla.android.design.font.fontScheme


private val colorPaletteL = lightColorScheme(
    primary = YallaBlack,
    secondary = Color.White,
    tertiary = YallaGray,
    background = Color.White,
    surface = YallaGray2,
    onPrimary = YallaBlack,
    onError = Color.Red
)

private val darkColorPaletteL = darkColorScheme(
    primary = YallaBlack,
    secondary = Color.White,
    tertiary = YallaGray,
    background = Color.White,
    surface = YallaGray2,
    onPrimary = YallaBlack,
    onError = Color.Red
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

    YallaCustomTheme(
        yallaLight(),
        fontScheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}