package uz.yalla.client.feature.intro.notification_permission.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.intro.notification_permission.view.NotificationPermissionRoute

internal const val NOTIFICATION_PERMISSION_ROUTE = "notification_permission_route"

internal fun NavGraphBuilder.notificationPermissionScreen(
    onPermissionGranted: () -> Unit
) {
    composable(
        route = NOTIFICATION_PERMISSION_ROUTE
    ) {
        NotificationPermissionRoute(onPermissionGranted = onPermissionGranted)
    }
}

internal fun NavController.navigateToNotificationPermissionScreen(navOptions: NavOptions? = null) =
    safeNavigate(NOTIFICATION_PERMISSION_ROUTE, navOptions)