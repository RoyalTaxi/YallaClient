package uz.ildam.technologies.yalla.android.ui.components.text_field

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

@Composable
fun AddressFormField(
    value: String,
    placeHolder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        modifier = modifier,
        onValueChange = onValueChange,
        textStyle = YallaTheme.font.label,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = YallaTheme.color.gray2,
            unfocusedContainerColor = YallaTheme.color.gray2,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = YallaTheme.color.black
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