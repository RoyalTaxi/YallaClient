package uz.yalla.client.feature.map.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import uz.yalla.client.feature.order.presentation.client_waiting.clientWaitingBottomSheet
import uz.yalla.client.feature.order.presentation.driver_waiting.driverWaitingBottomSheet
import uz.yalla.client.feature.order.presentation.feedback.feedbackBottomSheet
import uz.yalla.client.feature.order.presentation.main.MAIN_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.main.mainSheet
import uz.yalla.client.feature.order.presentation.no_service.noServiceSheet
import uz.yalla.client.feature.order.presentation.on_the_ride.onTheRideBottomSheet
import uz.yalla.client.feature.order.presentation.order_canceled.orderCanceledBottomSheet
import uz.yalla.client.feature.order.presentation.search.searchForCarBottomSheet

@Composable
fun BottomSheetNavHost(
    modifier: Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MAIN_SHEET_ROUTE
    ) {
        mainSheet()
        searchForCarBottomSheet()
        orderCanceledBottomSheet()
        clientWaitingBottomSheet()
        driverWaitingBottomSheet()
        onTheRideBottomSheet()
        feedbackBottomSheet()
        noServiceSheet()
    }
}