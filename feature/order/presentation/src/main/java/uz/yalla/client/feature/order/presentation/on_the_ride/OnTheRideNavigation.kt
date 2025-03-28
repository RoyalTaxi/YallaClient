package uz.yalla.client.feature.order.presentation.on_the_ride

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingSheet
import uz.yalla.client.feature.order.presentation.on_the_ride.model.OnTheRideSheetState
import uz.yalla.client.feature.order.presentation.on_the_ride.view.OnTheRideSheet

const val ORDER_ID = "order_id"
const val ON_THE_RIDE_ROUTE_BASE = "on_the_ride_bottom_sheet"
const val ON_THE_RIDE_ROUTE = "$ON_THE_RIDE_ROUTE_BASE?$ORDER_ID={$ORDER_ID}"

fun NavGraphBuilder.onTheRideBottomSheet() {
    composable(
        route = ON_THE_RIDE_ROUTE,
        arguments = listOf(navArgument(ORDER_ID) { type = NavType.IntType })
    ) {backStackEntry ->
        OnTheRideSheet.View(
            orderId = backStackEntry.arguments?.getInt(uz.yalla.client.feature.order.presentation.client_waiting.ORDER_ID).or0()
        )
    }
}

fun NavController.navigateToOnTheRideSheet(
    orderId: Int
){
    val route = "$ON_THE_RIDE_ROUTE_BASE?$ORDER_ID=$orderId"
    safeNavigate(
        route,
        navOptions {
            launchSingleTop = true
            restoreState = false
        }
    )
}