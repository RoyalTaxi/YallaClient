package uz.yalla.client.feature.notification.notifications.navigation

import androidx.activity.compose.BackHandler
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.common.navigation.getExitTransition
import uz.yalla.client.feature.notification.notifications.view.NotificationRoute

internal const val NOTIFICATIONS_ROUTE = "notifications_route"

internal fun NavGraphBuilder.notificationsScreen(
    onBack: () -> Unit,
    onClickNotification: (Int) -> Unit
) {
   composable(
       route = NOTIFICATIONS_ROUTE,
       exitTransition = { getExitTransition(isGoingBackToMap = true) },
   ) {
       BackHandler { onBack() }
       NotificationRoute(
           onNavigateBack = onBack,
           onClickNotification = onClickNotification
       )
   }
}
