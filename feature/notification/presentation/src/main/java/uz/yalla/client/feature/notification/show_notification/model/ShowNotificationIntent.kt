package uz.yalla.client.feature.notification.show_notification.model

internal sealed interface ShowNotificationIntent {
    data object NavigateBack : ShowNotificationIntent
}