package uz.yalla.client.feature.order.presentation.driver_waiting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingSheet

const val ORDER_ID = "order_id"
const val DRIVER_WAITING_ROUTE_BASE = "driver_waiting_bottom_sheet"
const val DRIVER_WAITING_ROUTE = "$DRIVER_WAITING_ROUTE_BASE?$ORDER_ID={$ORDER_ID}"

fun NavGraphBuilder.driverWaitingBottomSheet() {
    composable(
        route = DRIVER_WAITING_ROUTE,
        arguments = listOf(navArgument(ORDER_ID) { type = NavType.IntType})
    ){backStackEntry ->
        DriverWaitingSheet.View(
            orderId = backStackEntry.arguments?.getInt(ORDER_ID).or0()
        )
    }
}

fun NavController.navigateToDriverWaitingSheet(
    orderID: Int
) {
    val route = "$DRIVER_WAITING_ROUTE_BASE?$ORDER_ID=$orderID"
    safeNavigate(
        route,
        navOptions {
            launchSingleTop = true
            restoreState = false
            popUpTo(0) { inclusive = true }
        }
    )
}