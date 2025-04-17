package uz.yalla.client.feature.notification.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.notification.view.NotificationRoute

internal const val NOTIFICATION_ROUTE = "notification_route"

fun NavGraphBuilder.notificationsScreen(
    onBack: () -> Unit
) {
   composable(
       route = NOTIFICATION_ROUTE
   ) {
       NotificationRoute(
           onNavigateBack = onBack
       )
   }
}

fun NavController.navigateToNotificationScreen() = safeNavigate(NOTIFICATION_ROUTE)