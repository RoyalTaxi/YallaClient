package uz.yalla.client.feature.core.components.text_field

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import uz.yalla.client.feature.core.R
import uz.yalla.client.feature.core.components.buttons.ChooseFromMapButton
import uz.yalla.client.feature.core.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchLocationField(
    value: String?,
    onValueChange: (String) -> Unit,
    onClickMap: () -> Unit,
    modifier: Modifier = Modifier,
    isForDestination: Boolean
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults.cardColors(YallaTheme.color.gray2)
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
                    .background(
                        color = YallaTheme.color.primary,
                        shape = CircleShape
                    )
            ) else Box(
                modifier = Modifier
                    .size(8.dp)
                    .border(
                        shape = CircleShape,
                        width = 1.dp,
                        color = YallaTheme.color.gray
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = value.orEmpty(),
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = YallaTheme.font.labelLarge.copy(color = YallaTheme.color.black),
                decorationBox = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = value.orEmpty(),
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
                            focusedContainerColor = YallaTheme.color.gray2,
                            unfocusedContainerColor = YallaTheme.color.gray2,
                            errorContainerColor = YallaTheme.color.gray2,
                            disabledContainerColor = YallaTheme.color.gray2,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

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