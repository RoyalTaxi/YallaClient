package uz.yalla.client.feature.notification.notifications.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.feature.notification.notifications.view.NotificationRoute

internal const val NOTIFICATIONS_ROUTE = "notifications_route"

sealed interface FromNotifications {
    data object NavigateBack: FromNotifications
    data class NavigateDetails(val id: Int) : FromNotifications
}

internal fun NavGraphBuilder.notificationsScreen(
    fromNotifications: (FromNotifications) -> Unit
) {
   composable(route = NOTIFICATIONS_ROUTE) {
       NotificationRoute(fromNotifications)
   }
}
