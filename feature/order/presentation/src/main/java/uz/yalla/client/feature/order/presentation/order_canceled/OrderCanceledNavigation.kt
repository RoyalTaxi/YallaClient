package uz.yalla.client.feature.order.presentation.order_canceled

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.order.presentation.order_canceled.view.OrderCanceledSheet

const val ORDER_CANCELED_ROUTE = "order_canceled_route"

fun NavGraphBuilder.orderCanceledBottomSheet() {
    composable(ORDER_CANCELED_ROUTE) {
        OrderCanceledSheet.View()
    }
}

fun NavController.navigateToCanceledOrder(
    navOptions: NavOptions
) = safeNavigate(ORDER_CANCELED_ROUTE, navOptions)