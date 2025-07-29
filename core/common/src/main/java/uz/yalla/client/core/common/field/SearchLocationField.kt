package uz.yalla.client.core.common.field

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.button.ChooseFromMapButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchLocationField(
    value: String?,
    onValueChange: (String) -> Unit,
    onClickMap: () -> Unit,
    modifier: Modifier = Modifier,
    isForDestination: Boolean,
    isFocused: Boolean,
    clearDestination: () -> Unit,
    onFocusChange: (Boolean) -> Unit
) {

    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = value.orEmpty(),
                selection = TextRange(value?.length ?: 0)
            )
        )
    }

    LaunchedEffect(value) {
        launch(Dispatchers.Main) {
            textFieldValue = textFieldValue.copy(
                text = value.orEmpty(),
                selection = TextRange(value?.length ?: 0)
            )
        }
    }

    LaunchedEffect(isFocused) {
        launch(Dispatchers.Main) {
            if (isFocused) {
                focusRequester.requestFocus()
                textFieldValue =
                    textFieldValue.copy(selection = TextRange(textFieldValue.text.length))
            }
        }
    }

    Card(
        modifier = modifier
            .then(
                if (isFocused) Modifier.border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults.cardColors(YallaTheme.color.surface)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 16.dp)
                .height(IntrinsicSize.Min)
        ) {
            if (isForDestination) Box(
                modifier = Modifier
                    .size(8.dp)
                    .border(
                        shape = CircleShape,
                        width = 1.dp,
                        color = YallaTheme.color.gray
                    )
            ) else Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = YallaTheme.color.primary,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it.copy(selection = TextRange(it.text.length))
                    onValueChange(it.text)
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        onFocusChange(focusState.isFocused)
                        if (focusState.isFocused) {
                            textFieldValue =
                                textFieldValue.copy(selection = TextRange(textFieldValue.text.length))
                        }
                    },
                singleLine = true,
                textStyle = YallaTheme.font.labelLarge.copy(color = YallaTheme.color.onBackground),
                cursorBrush = SolidColor(YallaTheme.color.onSurface),
                decorationBox = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = textFieldValue.text,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = true,
                        contentPadding = PaddingValues(0.dp),
                        visualTransformation = VisualTransformation.None,
                        interactionSource = remember { MutableInteractionSource() },
                        placeholder = {
                            Text(
                                text =
                                    if (isForDestination) stringResource(R.string.where_to_go)
                                    else stringResource(R.string.enter_the_address),
                                color = YallaTheme.color.gray,
                                style = YallaTheme.font.label
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = YallaTheme.color.surface,
                            unfocusedContainerColor = YallaTheme.color.surface,
                            errorContainerColor = YallaTheme.color.surface,
                            disabledContainerColor = YallaTheme.color.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            if (textFieldValue.text.isNotEmpty() && isFocused) {
                IconButton(
                    onClick = {
                        textFieldValue = TextFieldValue("")
                        onValueChange("")
                        clearDestination()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear text",
                        tint = Color.Gray
                    )
                }
            }

            ChooseFromMapButton(
                onClick = onClickMap,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(4.dp)
                    .aspectRatio(1f)
            )
        }
    }
}