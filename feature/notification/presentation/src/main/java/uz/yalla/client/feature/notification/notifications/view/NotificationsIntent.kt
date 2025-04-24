package uz.yalla.client.feature.notification.notifications.view

internal sealed interface NotificationsIntent {
    data object OnNavigateBack: NotificationsIntent
    data class OnClickNotifications(val id: Int) : NotificationsIntent
}
