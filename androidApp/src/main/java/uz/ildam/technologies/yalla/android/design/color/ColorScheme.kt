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
    gray: Color,
    gray2: Color,
    grayBackground: Color,
    primary: Color,
) {
    var black by mutableStateOf(black, structuralEqualityPolicy())
    var white by mutableStateOf(white, structuralEqualityPolicy())
    var gray by mutableStateOf(gray, structuralEqualityPolicy())
    var gray2 by mutableStateOf(gray2, structuralEqualityPolicy())
    var grayBackground by mutableStateOf(grayBackground, structuralEqualityPolicy())
    var primary by mutableStateOf(primary, structuralEqualityPolicy())
}

fun yallaLight(
    black: Color = YallaBlack,
    white: Color = YallaWhite,
    gray: Color = YallaGray,
    gray2: Color = YallaGray2,
    grayBackground: Color = YallaGrayBackground,
    primary: Color = YallaPrimary
) = ColorScheme(
    black = black,
    white = white,
    gray = gray,
    gray2 = gray2,
    grayBackground = grayBackground,
    primary = primary
)

fun yallaDark(
    black: Color = YallaBlack,
    white: Color = YallaWhite,
    gray: Color = YallaGray,
    gray2: Color = YallaGray2,
    grayBackground: Color = YallaGrayBackground,
    primary: Color = YallaPrimary
) = ColorScheme(
    black = black,
    white = white,
    gray = gray,
    gray2 = gray2,
    grayBackground = grayBackground,
    primary = primary
)

val LocalCustomColorScheme = staticCompositionLocalOf { yallaLight() }
