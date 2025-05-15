package uz.yalla.client.core.common.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.R
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun AddressNotFound() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.address_not_found),
            style = YallaTheme.font.label,
            color = YallaTheme.color.gray
        )
    }
}
