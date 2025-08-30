package uz.yalla.client.feature.notification.show_notification.intent

import uz.yalla.client.feature.domain.model.NotificationModel

internal data class ShowNotificationState(
    val notification: NotificationModel?
) {
    companion object {
        val INTERNAL = ShowNotificationState(
            notification = null
        )
    }
}