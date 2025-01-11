package uz.ildam.technologies.yalla.android.ui.screens.permission

sealed interface PermissionIntent {
    data object GrantPermission : PermissionIntent
}