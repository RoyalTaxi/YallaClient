package uz.yalla.client.feature.notification.show_notification.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.notification.show_notification.view.ShowNotificationRoute

internal const val ID = "id"
internal const val SHOW_NOTIFICATION_ROUTE_BASE = "show_notification_base"
internal const val SHOW_NOTIFICATION_ROUTE = "$SHOW_NOTIFICATION_ROUTE_BASE?$ID={$ID}"

internal fun NavGraphBuilder.showNotificationScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = SHOW_NOTIFICATION_ROUTE,
        arguments = listOf(
            navArgument(ID) {
                type = NavType.IntType
            }
        )
    ) { navBackStackEntry ->
        val id = navBackStackEntry.arguments?.getInt(ID).or0()

        ShowNotificationRoute(
            id = id,
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToShowNotification(id: Int) {
    safeNavigate("$SHOW_NOTIFICATION_ROUTE_BASE?$ID=$id")
}