package uz.yalla.client.feature.notification.notifications.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.feature.notification.notifications.view.NotificationRoute

internal const val NOTIFICATIONS_ROUTE = "notifications_route"

internal fun NavGraphBuilder.notificationsScreen(
    onBack: () -> Unit,
    onClickNotification: (Int) -> Unit
) {
   composable(
       route = NOTIFICATIONS_ROUTE
   ) {
       NotificationRoute(
           onNavigateBack = onBack,
           onClickNotification = onClickNotification
       )
   }
}
