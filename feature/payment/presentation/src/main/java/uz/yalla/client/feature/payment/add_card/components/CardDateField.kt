package uz.yalla.client.feature.payment.add_card.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.payment.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CardDateField(
    date: String,
    modifier: Modifier = Modifier,
    onDateChange: (String) -> Unit
) {
    BasicTextField(
        value = date,
        modifier = modifier.width(75.dp),
        onValueChange = onDateChange,
        singleLine = true,
        textStyle = YallaTheme.font.label.copy(
            color = YallaTheme.color.black,
            textAlign = TextAlign.Center
        ),
        visualTransformation = PhoneVisualTransformation("XX/XX", 'X'),
        cursorBrush = SolidColor(YallaTheme.color.black),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = date,
                contentPadding = PaddingValues(10.dp),
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                visualTransformation = VisualTransformation.None,
                interactionSource = remember { MutableInteractionSource() },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = YallaTheme.color.black,
                    unfocusedTextColor = YallaTheme.color.black,
                    focusedContainerColor = YallaTheme.color.gray2,
                    unfocusedContainerColor = YallaTheme.color.gray2,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = YallaTheme.color.black
                ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.mm_yy),
                        style = YallaTheme.font.label,
                        color = YallaTheme.color.gray
                    )
                }
            )
        }
    )
}