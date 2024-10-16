package uz.ildam.technologies.yalla.android.components.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme


@Composable
fun CharView(
    index: Int,
    otpCount: Int,
    text: String,
    charColor: Color,
    highlightColor: Color,
    containerColor: Color,
    charSize: TextUnit,
    containerSize: Dp,
    containerRadius: Dp,
    type: Int = OTP_VIEW_TYPE_UNDERLINE,
    charBackground: Color = Color.Transparent,
    password: Boolean = false,
    passwordChar: String = ""
) {

    val borderColor = if (text.length == otpCount) {
        containerColor
    } else {
        if (index == text.length) highlightColor else containerColor
    }

    val modifier = if (type == OTP_VIEW_TYPE_BORDER) {
        Modifier
            .size(40.dp, 40.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(containerRadius)
            )
            .clip(RoundedCornerShape(containerRadius))
            .background(charBackground)
    } else Modifier
        .width(containerSize)
        .background(charBackground)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val char = when {
            index >= text.length -> ""
            password -> passwordChar
            else -> text[index].toString()
        }
        Text(
            text = char,
            color = charColor,
            modifier = modifier.wrapContentHeight(),
            style = YallaTheme.font.label,
            fontSize = charSize,
            textAlign = TextAlign.Center,
        )
    }
}