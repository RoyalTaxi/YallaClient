package uz.yalla.client.feature.android.intro.permission.view

internal sealed interface PermissionIntent {
    data object GrantPermission : PermissionIntent
}