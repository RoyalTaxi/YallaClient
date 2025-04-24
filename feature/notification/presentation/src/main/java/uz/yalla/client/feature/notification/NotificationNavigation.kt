package uz.yalla.client.feature.notification

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.core.presentation.navigation.safePopBackStack
import uz.yalla.client.feature.notification.notifications.navigation.NOTIFICATIONS_ROUTE
import uz.yalla.client.feature.notification.notifications.navigation.notificationsScreen
import uz.yalla.client.feature.notification.show_notification.navigation.navigateToShowNotification
import uz.yalla.client.feature.notification.show_notification.navigation.showNotificationScreen

internal const val NOTIFICATION_ROUTE = "notification_route"

fun NavGraphBuilder.notificationModule(
    navController: NavHostController
) {
    navigation(
        startDestination = NOTIFICATIONS_ROUTE,
        route = NOTIFICATION_ROUTE
    ) {
        notificationsScreen(
            onBack = navController::safePopBackStack,
            onClickNotification = navController::navigateToShowNotification
        )

        showNotificationScreen(
            onNavigateBack = navController::safePopBackStack
        )
    }
}

fun NavController.navigateToNotificationModule() = safeNavigate(NOTIFICATION_ROUTE)