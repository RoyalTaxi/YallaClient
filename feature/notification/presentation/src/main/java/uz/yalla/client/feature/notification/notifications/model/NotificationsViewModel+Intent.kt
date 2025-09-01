package uz.yalla.client.feature.notification.notifications.model

import uz.yalla.client.feature.notification.notifications.intent.NotificationsIntent
import uz.yalla.client.feature.notification.notifications.intent.NotificationsSideEffect

internal fun NotificationsViewModel.onIntent(intent: NotificationsIntent) = intent {
    when (intent) {
        is NotificationsIntent.OnClickNotifications -> postSideEffect(
            NotificationsSideEffect.NavigateDetails(
                intent.id
            )
        )

        NotificationsIntent.OnNavigateBack -> postSideEffect(NotificationsSideEffect.NavigateBack)
    }
}