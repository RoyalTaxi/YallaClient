package uz.yalla.client.feature.places.place.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
internal fun AddressFormField(
    value: String,
    placeHolder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        modifier = modifier,
        onValueChange = onValueChange,
        textStyle = YallaTheme.font.label.copy(color = YallaTheme.color.onBackground),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = YallaTheme.color.surface,
            unfocusedContainerColor = YallaTheme.color.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = YallaTheme.color.onBackground
        ),
        placeholder = {
            Text(
                text = placeHolder,
                color = YallaTheme.color.gray,
                style = YallaTheme.font.label
            )
        },
        shape = RoundedCornerShape(16.dp),
    )
}