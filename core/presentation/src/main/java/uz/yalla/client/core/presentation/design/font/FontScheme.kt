package uz.yalla.client.core.presentation.design.font

import androidx.compose.ui.text.TextStyle

data class FontScheme(
    val headline: TextStyle = TextStyle(),
    val title: TextStyle = TextStyle(),
    val title2: TextStyle = TextStyle(),
    val body: TextStyle = TextStyle(),
    val bodySmall: TextStyle = TextStyle(),
    val label: TextStyle = TextStyle(),
    val labelSemiBold: TextStyle = TextStyle(),
    val labelLarge: TextStyle = TextStyle(),
    val customNumber: TextStyle = TextStyle(),
    val customNumberText: TextStyle = TextStyle()
)