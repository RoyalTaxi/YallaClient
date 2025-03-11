package uz.yalla.client.feature.android.payment.top_up_balance.components.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.android.payment.R
import uz.yalla.client.feature.android.payment.top_up_balance.components.transformation.TopUpBalanceVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BalanceInputField(
    balance: String,
    modifier: Modifier = Modifier,
    onBalanceChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            modifier = modifier
                .weight(1f, false)
                .width(IntrinsicSize.Min)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused && balance.isEmpty()) {
                        focusRequester.requestFocus()
                    } },
            value = balance,
            onValueChange = { newValue ->
                val filteredValue = newValue.filter { it.isDigit() }

                val maxAmount = 99_999_999
                val newAmount = filteredValue.take(8).toIntOrNull() ?: 0

                if (newAmount <= maxAmount) {
                    onBalanceChange(newAmount.toString())
                }
            },
            singleLine = true,
            visualTransformation = TopUpBalanceVisualTransformation(context),
            textStyle = YallaTheme.font.headline.copy(color = YallaTheme.color.black),
            cursorBrush = SolidColor(YallaTheme.color.black),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            decorationBox = { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = balance,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = TopUpBalanceVisualTransformation(context),
                    interactionSource = remember { MutableInteractionSource() },
                    isError = false,
                    label = null,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.enter_balance),
                            color = YallaTheme.color.gray,
                            style = YallaTheme.font.headline,
                            maxLines = 1,
                            modifier = Modifier.width(100.dp)
                        )
                    },
                    contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(0.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,

                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        )

        if (balance.isNotEmpty()) Text(
            text = stringResource(R.string.currency),
            color = YallaTheme.color.black,
            style = YallaTheme.font.headline,
        )
    }
}