package uz.ildam.technologies.yalla.android.ui.components.text_field

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchLocationField(
    value: String?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults.cardColors(YallaTheme.color.gray2)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = YallaTheme.color.primary,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = value.orEmpty(),
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
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
                                text = stringResource(R.string.where_to_go),
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
        }
    }
}