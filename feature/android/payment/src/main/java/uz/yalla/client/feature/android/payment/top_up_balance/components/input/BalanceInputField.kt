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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import uz.yalla.client.feature.android.payment.R
import uz.yalla.client.feature.android.payment.top_up_balance.components.transformation.TopUpBalanceVisualTransformation
import uz.yalla.client.feature.core.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BalanceInputField(
    balance: String,
    modifier: Modifier = Modifier,
    onBalanceChange: (String) -> Unit
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            modifier = modifier
                .weight(1f, false)
                .width(IntrinsicSize.Min),
            value = balance,
            onValueChange = onBalanceChange,
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

                    }
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