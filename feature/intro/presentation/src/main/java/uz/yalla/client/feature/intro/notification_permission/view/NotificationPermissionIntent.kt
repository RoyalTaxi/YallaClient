package uz.yalla.client.feature.intro.notification_permission.view

internal sealed interface NotificationPermissionIntent {
    data object GrantPermission : NotificationPermissionIntent

}