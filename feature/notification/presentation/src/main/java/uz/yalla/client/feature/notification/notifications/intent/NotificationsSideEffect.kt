package uz.yalla.client.feature.notification.notifications.intent

sealed interface NotificationsSideEffect {
    data object NavigateBack: NotificationsSideEffect
    data class NavigateDetails(val id: Int): NotificationsSideEffect
}