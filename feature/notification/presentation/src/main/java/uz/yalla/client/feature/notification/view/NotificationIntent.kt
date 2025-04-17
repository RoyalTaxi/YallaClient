package uz.yalla.client.feature.notification.view

internal sealed interface NotificationIntent {
    data object OnNavigateBack: NotificationIntent
}
