package uz.yalla.client.feature.home.presentation.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.home.presentation.R

@Composable
fun NoInternetCard(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(YallaTheme.color.red)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = stringResource(R.string.no_connection),
                style = YallaTheme.font.labelSemiBold,
                color = YallaTheme.color.onRed
            )

            Text(
                text = stringResource(R.string.check_your_connection),
                style = YallaTheme.font.bodySmall,
                color = YallaTheme.color.onRed
            )
        }
    }
}