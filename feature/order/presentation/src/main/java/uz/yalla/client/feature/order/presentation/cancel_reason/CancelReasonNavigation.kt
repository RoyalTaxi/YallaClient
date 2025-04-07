package uz.yalla.client.feature.order.presentation.cancel_reason

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.presentation.navigation.safeNavigate

const val ORDER_ID = "order_id"
const val CANCEL_REASON_ROUTE_BASE = "cancel_reason_route"
const val CANCEL_REASON_ROUTE = "$CANCEL_REASON_ROUTE_BASE?$ORDER_ID={$ORDER_ID}"

fun NavGraphBuilder.cancelReasonScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = CANCEL_REASON_ROUTE,
        arguments = listOf(
            navArgument(ORDER_ID) { type = NavType.IntType }
        )
    ) { backStackEntry ->
        CancelReasonRoute(
            orderId = backStackEntry.arguments?.getInt(ORDER_ID).or0(),
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToCancelReasonScreen(
    orderId: Int
) {
    val route = "$CANCEL_REASON_ROUTE_BASE?$ORDER_ID=$orderId"
    safeNavigate(route)
}