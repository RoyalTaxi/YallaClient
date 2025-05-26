package uz.yalla.client.feature.order.presentation.client_waiting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingSheet

const val ORDER_ID = "order_id"
const val CLIENT_WAITING_ROUTE_BASE = "client_waiting_bottom_sheet"
const val CLIENT_WAITING_ROUTE = "$CLIENT_WAITING_ROUTE_BASE?$ORDER_ID={$ORDER_ID}"

fun NavGraphBuilder.clientWaitingBottomSheet() {
    composable(
        route = CLIENT_WAITING_ROUTE,
        arguments = listOf(navArgument(ORDER_ID) { type = NavType.IntType })
    ) { backStackEntry ->
        ClientWaitingSheet(
            orderId = backStackEntry.arguments?.getInt(ORDER_ID).or0()
        )
    }
}

fun NavController.navigateToClientWaitingSheet(
    orderId: Int
) {
    val route = "$CLIENT_WAITING_ROUTE_BASE?$ORDER_ID=$orderId"
    safeNavigate(
        route,
        navOptions {
            launchSingleTop = true
            restoreState = false
            popUpTo(0) { inclusive = true }
        }
    )
}