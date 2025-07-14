package uz.yalla.client.feature.intro.notification_permission.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.shape.squareSize
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.intro.R

@Composable
internal fun NotificationPermissionScreen(
    scrollState: ScrollState,
    onIntent: (NotificationPermissionIntent) -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.background,
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(43.dp))

            NotificationPermissionContent()

            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(56.dp))

            NotificationPermissionFooter(
                onClickSkip = { onIntent(NotificationPermissionIntent.Skip) },
                onClickPermission = { onIntent(NotificationPermissionIntent.GrantPermission) }
            )
        }
    }
}

@Composable
private fun NotificationPermissionContent ()
{
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .squareSize()
    ) {
        Image(
            painter = painterResource(R.drawable.ic_notification_permission),
            contentDescription = null,
            modifier = Modifier.clip(CircleShape)
        )
    }

    Spacer(modifier = Modifier.height(54.dp))

    Text(
        text = stringResource(id = R.string.enable_notification),
        color = YallaTheme.color.onBackground,
        style = YallaTheme.font.headline,
        modifier = Modifier.padding(horizontal = 20.dp)
    )

    Spacer(modifier = Modifier.height(32.dp))

    Text(
        text = stringResource(id = R.string.enable_notification_body),
        color = YallaTheme.color.gray,
        style = YallaTheme.font.body,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
private fun NotificationPermissionFooter(
    onClickSkip: () -> Unit,
    onClickPermission: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(
            onClick = onClickSkip,
            shape = CircleShape
        ) {
            Text(
                text = stringResource(R.string.skip),
                color = YallaTheme.color.primary,
                style = YallaTheme.font.body
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        PrimaryButton(
            text = stringResource(id = R.string.next),
            onClick = onClickPermission,
            contentColor = YallaTheme.color.onPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
