package uz.yalla.client.core.common.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun SelectCurrentLocationButton(
    text: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Button(
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(YallaTheme.color.gray2),
        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp),
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.invoke()

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                color = YallaTheme.color.black,
                style = YallaTheme.font.labelLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Spacer(modifier = Modifier.width(8.dp))

            trailingIcon?.invoke()
        }
    }
}