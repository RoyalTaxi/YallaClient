package uz.yalla.client.feature.home.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import uz.yalla.client.feature.order.presentation.cancel_reason.cancelReasonSheet
import uz.yalla.client.feature.order.presentation.client_waiting.clientWaitingBottomSheet
import uz.yalla.client.feature.order.presentation.driver_waiting.driverWaitingBottomSheet
import uz.yalla.client.feature.order.presentation.feedback.feedbackBottomSheet
import uz.yalla.client.feature.order.presentation.main.MAIN_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.main.mainSheet
import uz.yalla.client.feature.order.presentation.no_service.noServiceSheet
import uz.yalla.client.feature.order.presentation.on_the_ride.onTheRideBottomSheet
import uz.yalla.client.feature.order.presentation.order_canceled.orderCanceledBottomSheet
import uz.yalla.client.feature.order.presentation.search.searchForCarBottomSheet

const val ORDER_ID = "order_id"

@Composable
fun BottomSheetNavHost(
    modifier: Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MAIN_SHEET_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
    ) {
        mainSheet()
        searchForCarBottomSheet()
        orderCanceledBottomSheet()
        clientWaitingBottomSheet()
        driverWaitingBottomSheet()
        onTheRideBottomSheet()
        feedbackBottomSheet()
        noServiceSheet()
        cancelReasonSheet()
    }
}

fun NavController.shouldNavigateToSheet(
    routePattern: String,
    orderId: Int?
): Boolean {
    val currentDestination = this.currentDestination
    val thisRoute = currentDestination?.route ?: ""

    if (!thisRoute.contains(routePattern)) {
        return true
    }

    val currentBackStackEntry = this.currentBackStackEntry
    val currentOrderId = currentBackStackEntry?.arguments?.getInt(ORDER_ID, -1) ?: -1

    return currentOrderId != orderId
}