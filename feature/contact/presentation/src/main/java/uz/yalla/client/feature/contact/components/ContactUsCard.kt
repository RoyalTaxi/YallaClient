package uz.yalla.client.feature.contact.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.setting.domain.model.SocialNetwork

@Composable
fun ContactUsCard(
    socialNetwork: SocialNetwork,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RectangleShape,
        colors = CardDefaults.cardColors(YallaTheme.color.background),
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(vertical = 18.dp, horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(socialNetwork.iconResId),
                tint = Color.Unspecified,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
            )

            Text(
                text = stringResource(socialNetwork.titleResId),
                style = YallaTheme.font.labelLarge,
                color = YallaTheme.color.onBackground,
                textAlign = TextAlign.Center
            )
        }
    }
}