package uz.yalla.client.feature.notification.notifications.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import uz.yalla.client.core.common.topbar.CenterAlignedScrollableTopBar
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.domain.model.NotificationModel
import uz.yalla.client.feature.notification.notifications.components.NotificationItem
import uz.yalla.client.feature.notification.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationScreen(
    notifications: LazyPagingItems<NotificationModel>,
    onIntent: (NotificationsIntent) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        containerColor = YallaTheme.color.background,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedScrollableTopBar(
                title = stringResource(R.string.notifications),
                collapsedTitleTextStyle = YallaTheme.font.labelLarge,
                expandedTitleTextStyle = YallaTheme.font.headline,
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = YallaTheme.color.background,
                    scrolledContainerColor = YallaTheme.color.background
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { onIntent(NotificationsIntent.OnNavigateBack) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = YallaTheme.color.onBackground
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            NotificationContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                notifications = notifications,
                onClickNotification = { id -> onIntent(NotificationsIntent.OnClickNotifications(id)) }
            )
        }
    )
}

@Composable
private fun NotificationContent(
    modifier: Modifier,
    onClickNotification: (Int) -> Unit,
    notifications: LazyPagingItems<NotificationModel>
) {
    when {
        notifications.itemCount == 0 -> {
            Column(modifier = modifier) {
                Spacer(modifier = Modifier.height(70.dp))

                EmptyNotifications()
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications.itemCount) { index ->
                    notifications[index]?.let { notification ->
                        NotificationItem(
                            notification = notification,
                            isRead = notification.isRead,
                            onClick = { onClickNotification(notification.id) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun EmptyNotifications() {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.img_no_notification),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .size(160.dp)
        )

        Text(
            text = stringResource(R.string.no_notification_body),
            style = YallaTheme.font.label,
            color = YallaTheme.color.gray
        )
    }
}