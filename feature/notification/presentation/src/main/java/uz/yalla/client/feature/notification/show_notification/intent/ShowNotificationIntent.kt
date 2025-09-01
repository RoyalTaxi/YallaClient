package uz.yalla.client.feature.notification.show_notification.intent

internal sealed interface ShowNotificationIntent {
    data object NavigateBack : ShowNotificationIntent
}