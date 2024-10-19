package uz.ildam.technologies.yalla.android.ui.components.otp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

const val OTP_VIEW_TYPE_UNDERLINE = 1
const val OTP_VIEW_TYPE_BORDER = 2

@Composable
fun OtpView(
    otpText: String,
    modifier: Modifier = Modifier,
    charColor: Color = YallaTheme.color.black,
    containerColor: Color = Color.Transparent,
    selectedContainerColor: Color = YallaTheme.color.black,
    charBackground: Color = YallaTheme.color.gray2,
    charSize: TextUnit = 14.sp,
    containerSize: Dp = charSize.value.dp * 2,
    containerRadius: Dp = 12.dp,
    containerSpacing: Dp = 12.dp,
    otpCount: Int = 5,
    type: Int = OTP_VIEW_TYPE_BORDER,
    enabled: Boolean = true,
    password: Boolean = false,
    passwordChar: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.NumberPassword,
        imeAction = ImeAction.Done
    ),
    onOtpTextChange: (String) -> Unit
) {
    BasicTextField(
        modifier = modifier,
        value = otpText,
        onValueChange = {
            if (it.length <= otpCount) {
                onOtpTextChange.invoke(it)
            }
        },
        cursorBrush = SolidColor(YallaTheme.color.black),
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(containerSpacing)) {
                repeat(otpCount) { index ->
                    CharView(
                        index = index,
                        otpCount = otpCount,
                        text = otpText,
                        charColor = charColor,
                        containerColor = containerColor,
                        highlightColor = selectedContainerColor,
                        charSize = charSize,
                        containerRadius = containerRadius,
                        containerSize = containerSize,
                        type = type,
                        charBackground = charBackground,
                        password = password,
                        passwordChar = passwordChar,
                    )
                }
            }
        }
    )
}
