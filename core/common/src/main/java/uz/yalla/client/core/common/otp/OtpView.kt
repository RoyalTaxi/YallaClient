package uz.yalla.client.core.common.otp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.yalla.client.core.presentation.design.theme.YallaTheme

const val OTP_VIEW_TYPE_UNDERLINE = 1
const val OTP_VIEW_TYPE_BORDER = 2

@Composable
fun OtpView(
    otpText: String,
    modifier: Modifier = Modifier,
    charColor: Color = YallaTheme.color.black,
    containerColor: Color = Color.Transparent,
    selectedContainerColor: Color = YallaTheme.color.primary,
    charBackground: Color = YallaTheme.color.gray2,
    charSize: TextUnit = 14.sp,
    containerRadius: Dp = 12.dp,
    otpCount: Int = 5,
    type: Int = OTP_VIEW_TYPE_BORDER,
    enabled: Boolean = true,
    password: Boolean = false,
    passwordChar: String = "",
    onOtpTextChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.NumberPassword,
        imeAction = ImeAction.Done
    )
) {
    val focusRequester = remember { FocusRequester() }

    val textFieldValue = remember(otpText) {
        TextFieldValue(
            text = otpText,
            selection = TextRange(otpText.length)
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BasicTextField(
        modifier = modifier.focusRequester(focusRequester),
        value = textFieldValue,
        onValueChange = { newValue ->
            if (newValue.text != otpText) {
                val filteredText = newValue.text.filter { it.isDigit() }.take(otpCount)
                onOtpTextChange.invoke(filteredText)
            }
        },
        cursorBrush = SolidColor(YallaTheme.color.black),
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.width(IntrinsicSize.Min)
            ) {
                repeat(otpCount) { index ->
                    CharView(
                        index = index,
                        modifier = Modifier,
                        otpCount = otpCount,
                        text = otpText,
                        charColor = charColor,
                        containerColor = containerColor,
                        highlightColor = selectedContainerColor,
                        charSize = charSize,
                        containerRadius = containerRadius,
                        type = type,
                        charBackground = charBackground,
                        password = password,
                        passwordChar = passwordChar,
                    )

                    if (index != otpCount - 1) Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    )
}