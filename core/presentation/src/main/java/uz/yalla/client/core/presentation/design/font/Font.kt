package uz.yalla.client.core.presentation.design.font

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import uz.yalla.client.core.presentation.R.font.inter_regular as regular
import uz.yalla.client.core.presentation.R.font.inter_semi_bold as semi_bold
import uz.yalla.client.core.presentation.R.font.nummernschild as custom


val fontScheme = FontScheme(
    headline = createTextStyle(
        fontResId = semi_bold,
        weight = FontWeight.W600,
        size = 32
    ),
    title = createTextStyle(
        fontResId = semi_bold,
        weight = FontWeight.W600,
        size = 22
    ),
    title2 = createTextStyle(
        fontResId = semi_bold,
        weight = FontWeight.W600,
        size = 20
    ),
    body = createTextStyle(
        fontResId = regular,
        weight = FontWeight.W400,
        size = 12
    ),
    bodySmall = createTextStyle(
        fontResId = regular,
        weight = FontWeight.W400,
        size = 12
    ),
    label = createTextStyle(
        fontResId = regular,
        weight = FontWeight.W400,
        size = 14
    ),
    labelLarge = createTextStyle(
        fontResId = semi_bold,
        weight = FontWeight.W600,
        size = 16
    ),
    labelSemiBold = createTextStyle(
        fontResId = semi_bold,
        weight = FontWeight.W600,
        size = 16
    ),
    customNumber = TextStyle(
        fontFamily = FontFamily(Font(custom)),
        fontSize = 18.sp,
        fontWeight = FontWeight.W500
    ),
    customNumberText = createTextStyle(
        fontResId = semi_bold,
        weight = FontWeight.W400,
        size = 7
    )
)

val LocalCustomTypography = staticCompositionLocalOf { fontScheme }

private fun createTextStyle(
    fontResId: Int,
    weight: FontWeight,
    size: Int
): TextStyle {
    return TextStyle(
        fontFamily = FontFamily(Font(fontResId)),
        fontWeight = weight,
        fontSize = size.sp
    )
}