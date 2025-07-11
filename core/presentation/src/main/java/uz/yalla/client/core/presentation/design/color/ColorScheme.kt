package uz.yalla.client.core.presentation.design.color

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

class ColorScheme(
    primary: Color,
    dynamicPrimary: Color,
    inverseDynamicPrimary: Color,
    onPrimary: Color,
    background: Color,
    onBackground: Color,
    surface: Color,
    onSurface: Color,
    black: Color,
    onBlack: Color,
    gray: Color,
    red: Color,
    onRed: Color
) {
    var primary by mutableStateOf(primary, structuralEqualityPolicy())
    var dynamicPrimary by mutableStateOf(dynamicPrimary, structuralEqualityPolicy())
    var inverseDynamicPrimary by mutableStateOf(inverseDynamicPrimary, structuralEqualityPolicy())
    var onPrimary by mutableStateOf(onPrimary, structuralEqualityPolicy())
    var background by mutableStateOf(background, structuralEqualityPolicy())
    var onBackground by mutableStateOf(onBackground, structuralEqualityPolicy())
    var surface by mutableStateOf(surface, structuralEqualityPolicy())
    var onSurface by mutableStateOf(onSurface, structuralEqualityPolicy())
    var black by mutableStateOf(black, structuralEqualityPolicy())
    var onBlack by mutableStateOf(onBlack, structuralEqualityPolicy())
    var gray by mutableStateOf(gray, structuralEqualityPolicy())
    var red by mutableStateOf(red, structuralEqualityPolicy())
    var onRed by mutableStateOf(onRed, structuralEqualityPolicy())

}

fun yallaLight(
    primary: Color = primaryDay,
    dynamicPrimary: Color = dynamicPrimaryDay,
    inverseDynamicPrimary: Color = inverseDynamicPrimaryDay,
    onPrimary: Color = onPrimaryDay,
    background: Color = backgroundDay,
    onBackground: Color = onBackgroundDay,
    surface: Color = surfaceDay,
    onSurface: Color = onSurfaceDay,
    black: Color = blackDay,
    onBlack: Color = onBlackDay,
    gray: Color = grayDay,
    red: Color = redDay,
    onRed: Color = onRedDay,
) = ColorScheme(
    primary = primary,
    dynamicPrimary = dynamicPrimary,
    inverseDynamicPrimary = inverseDynamicPrimary,
    onPrimary = onPrimary,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    black = black,
    onBlack = onBlack,
    gray = gray,
    red = red,
    onRed = onRed
)

fun yallaDark(
    primary: Color = primaryNight,
    dynamicPrimary: Color = dynamicPrimaryNight,
    inverseDynamicPrimary: Color = inverseDynamicPrimaryNight,
    onPrimary: Color = onPrimaryNight,
    background: Color = backgroundNight,
    onBackground: Color = onBackgroundNight,
    surface: Color = surfaceNight,
    onSurface: Color = onSurfaceNight,
    black: Color = blackNight,
    onBlack: Color = onBlackNight,
    gray: Color = grayNight,
    red: Color = redNight,
    onRed: Color = onRedNight
) = ColorScheme(
    primary = primary,
    dynamicPrimary = dynamicPrimary,
    inverseDynamicPrimary = inverseDynamicPrimary,
    onPrimary = onPrimary,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    black = black,
    onBlack = onBlack,
    gray = gray,
    red = red,
    onRed = onRed
)

val LocalCustomColorScheme = staticCompositionLocalOf { yallaDark() }
