package uz.ildam.technologies.yalla.android.design.color

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color

class ColorScheme(
    black: Color,
    white: Color,
    grey: Color,
    grey2: Color
) {
    var black by mutableStateOf(black, structuralEqualityPolicy())
    var white by mutableStateOf(white, structuralEqualityPolicy())
    var grey by mutableStateOf(grey, structuralEqualityPolicy())
    var grey2 by mutableStateOf(grey2, structuralEqualityPolicy())
}

fun yallaLight(
    black: Color = Black,
    white: Color = White,
    grey: Color = Grey,
    grey2: Color = Grey2
) = ColorScheme(
    black = black,
    white = white,
    grey = grey,
    grey2 = grey2
)

fun yallaDark(
    black: Color = Black,
    white: Color = White,
    grey: Color = Grey,
    grey2: Color = Grey2
) = ColorScheme(
    black = black,
    white = white,
    grey = grey,
    grey2 = grey2
)

val LocalCustomColorScheme = staticCompositionLocalOf { yallaLight() }
