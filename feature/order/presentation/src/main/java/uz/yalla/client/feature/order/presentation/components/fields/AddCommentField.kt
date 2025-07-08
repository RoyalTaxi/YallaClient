package uz.yalla.client.feature.order.presentation.components.fields

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommentField(
    commentText: String,
    onCommentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        value = commentText,
        onValueChange = onCommentChange,
        modifier = modifier,
        singleLine = false,
        textStyle = YallaTheme.font.label.copy(color = YallaTheme.color.gray),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = commentText,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = false,
                contentPadding = PaddingValues(0.dp),
                visualTransformation = VisualTransformation.None,
                interactionSource = remember { MutableInteractionSource() },
                placeholder = {
                    Text(
                        text = stringResource(R.string.write_comment_here),
                        color = YallaTheme.color.gray,
                        style = YallaTheme.font.label
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = YallaTheme.color.background,
                    unfocusedContainerColor = YallaTheme.color.background,
                    errorContainerColor = YallaTheme.color.background,
                    disabledContainerColor = YallaTheme.color.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        }
    )
}