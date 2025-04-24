package uz.yalla.client.core.common.field

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimaryTextField(
    text: String,
    placeHolderText: String,
    onChangeText: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: Painter? = null,
    onClick: () -> Unit = {}
) {

    var isFocused by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        enabled = trailingIcon != null,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = YallaTheme.color.gray2,
            disabledContainerColor = YallaTheme.color.gray2,
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        vertical = if (isFocused || text.isNotBlank()) 10.dp else 16.dp
                    )
            ) {
                if ((isFocused || text.isNotBlank()) || (trailingIcon != null && text.isNotBlank()))
                    Text(
                        text = placeHolderText,
                        color = YallaTheme.color.gray,
                        style = YallaTheme.font.bodySmall
                    )

                BasicTextField(
                    value = text,
                    onValueChange = onChangeText,
                    cursorBrush = SolidColor(YallaTheme.color.black),
                    textStyle = YallaTheme.font.labelSemiBold.copy(color = YallaTheme.color.black),
                    enabled = trailingIcon == null,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused },
                    decorationBox = { innerTextField ->
                        TextFieldDefaults.DecorationBox(
                            value = text,
                            innerTextField = innerTextField,
                            placeholder = {
                                if (isFocused.not()) Text(
                                    text = placeHolderText,
                                    style = YallaTheme.font.label,
                                    color = YallaTheme.color.gray
                                )
                            },
                            contentPadding = PaddingValues(0.dp),
                            enabled = true,
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            visualTransformation = VisualTransformation.None,
                            interactionSource = remember { MutableInteractionSource() },
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
            }

            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    painter = trailingIcon,
                    contentDescription = null,
                    tint = YallaTheme.color.black
                )
            }
        }
    }
}