package uz.yalla.client.feature.notification.notifications.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import uz.yalla.client.core.common.utils.getRelativeDate
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.domain.model.NotificationModel
import uz.yalla.client.feature.notification.presentation.R

@Composable
internal fun NotificationItem(
    notification: NotificationModel,
    isExpanded: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = YallaTheme.color.white,
            disabledContainerColor = YallaTheme.color.white),
        enabled = !isExpanded,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            AsyncImage(
                model = notification.image,
                contentDescription = null,
                error = painterResource(R.drawable.ic_default),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = getRelativeDate(
                    date = notification.dateTime,
                    today = stringResource(R.string.today),
                    yesterday = stringResource(R.string.yesterday)
                ),
                style = YallaTheme.font.body,
                color = YallaTheme.color.gray
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = notification.title,
                style = YallaTheme.font.title2,
                color = YallaTheme.color.black
            )

            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = notification.content,
                    style = YallaTheme.font.body,
                    color = YallaTheme.color.black
                )
            }
        }
    }
}