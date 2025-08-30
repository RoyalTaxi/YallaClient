package uz.yalla.client.feature.notification.show_notification.intent

sealed interface ShowNotificationSideEffect {
    data object NavigateBack: ShowNotificationSideEffect
}