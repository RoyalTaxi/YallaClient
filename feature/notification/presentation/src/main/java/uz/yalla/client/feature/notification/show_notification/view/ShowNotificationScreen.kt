package uz.yalla.client.feature.notification.show_notification.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.notification.notifications.components.NotificationItem
import uz.yalla.client.feature.notification.presentation.R
import uz.yalla.client.feature.notification.show_notification.model.ShowNotificationIntent

@Composable
internal fun ShowNotificationScreen(
    state: ShowNotificationUIState,
    onIntent: (ShowNotificationIntent) -> Unit
){
    Scaffold(
        containerColor = YallaTheme.color.background,
        topBar = { ShowNotificationTopBar { onIntent(ShowNotificationIntent.NavigateBack) } },
        content = {paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                state.notification?.let {
                    NotificationItem(
                        notification = it,
                        isExpanded = true,
                        onClick = {}
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowNotificationTopBar(
    onNavigateBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(YallaTheme.color.background),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = YallaTheme.color.onBackground
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.notifications),
                color = YallaTheme.color.onBackground,
                style = YallaTheme.font.labelLarge
            )
        }
    )
}