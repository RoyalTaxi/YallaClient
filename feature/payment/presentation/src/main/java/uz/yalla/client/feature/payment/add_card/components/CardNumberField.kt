package uz.yalla.client.feature.payment.add_card.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.payment.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CardNumberField(
    number: String,
    modifier: Modifier = Modifier,
    onClickCamera: () -> Unit,
    onNumberChange: (String) -> Unit
) {
    BasicTextField(
        value = number,
        modifier = modifier,
        onValueChange = onNumberChange,
        singleLine = true,
        textStyle = YallaTheme.font.label.copy(color = YallaTheme.color.onSurface),
        visualTransformation = PhoneVisualTransformation("xxxx xxxx xxxx xxxx", 'x'),
        cursorBrush = SolidColor(YallaTheme.color.onSurface),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = number,
                contentPadding = PaddingValues(12.dp),
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                visualTransformation = VisualTransformation.None,
                interactionSource = remember { MutableInteractionSource() },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = YallaTheme.color.onSurface,
                    unfocusedTextColor = YallaTheme.color.onSurface,
                    focusedContainerColor = YallaTheme.color.background,
                    unfocusedContainerColor = YallaTheme.color.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = YallaTheme.color.onBackground
                ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.card_number),
                        style = YallaTheme.font.label,
                        color = YallaTheme.color.gray
                    )
                },
//                trailingIcon = {
//                    IconButton(onClick = onClickCamera) {
//                        Icon(
//                            painter = painterResource(R.drawable.ic_scan_camera),
//                            contentDescription = null
//                        )
//                    }
//                }
            )
        }
    )
}