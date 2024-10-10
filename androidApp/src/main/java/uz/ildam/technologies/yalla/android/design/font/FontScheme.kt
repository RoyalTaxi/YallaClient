package uz.ildam.technologies.yalla.android.design.font

import androidx.compose.ui.text.TextStyle

data class FontScheme(
    val headline: TextStyle = TextStyle(),
    val title: TextStyle = TextStyle(),
    val body: TextStyle = TextStyle(),
    val bodySmall: TextStyle = TextStyle(),
    val label: TextStyle = TextStyle(),
    val labelSemiBold: TextStyle = TextStyle(),
    val labelLarge: TextStyle = TextStyle(),
)