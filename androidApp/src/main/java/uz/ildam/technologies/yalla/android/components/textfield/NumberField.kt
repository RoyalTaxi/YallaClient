package uz.ildam.technologies.yalla.android.components.textfield

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.components.transformation.PhoneVisualTransformation
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberTextField(
    number: String,
    modifier: Modifier = Modifier,
    onUpdateNumber: (String) -> Unit
) {
    val focusedColor = YallaTheme.color.black
    val unfocusedColor = YallaTheme.color.gray2
    var borderColor by remember { mutableStateOf(unfocusedColor) }

    BasicTextField(
        value = number,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
                borderColor = if (it.isFocused) focusedColor
                else unfocusedColor
            }
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        onValueChange = onUpdateNumber,
        textStyle = YallaTheme.font.label.copy(color = YallaTheme.color.black),
        visualTransformation = PhoneVisualTransformation("(00) 000-00-00", '0'),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Done
        ),
        cursorBrush = SolidColor(YallaTheme.color.black),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = number,
                contentPadding = PaddingValues(vertical = 22.dp, horizontal = 16.dp),
                innerTextField = innerTextField,
                prefix = {
                    Text(
                        text = "+998 ",
                        style = YallaTheme.font.label,
                        color = YallaTheme.color.black
                    )
                },
                placeholder = {
                    Text(
                        text = "91 123 45 67",
                        style = YallaTheme.font.label,
                        color = YallaTheme.color.gray
                    )
                },
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
                )
            )
        }
    )
}
