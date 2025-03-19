package uz.yalla.client.feature.intro.permission.view

internal sealed interface PermissionIntent {
    data object GrantPermission : PermissionIntent
}