package uz.yalla.client.feature.order.presentation.feedback

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.order.presentation.feedback.view.FeedbackSheet

const val ORDER_ID = "order_id"
const val FEEDBACK_ROUTE_BASE = "feedback_bottom_sheet"
const val FEEDBACK_ROUTE = "$FEEDBACK_ROUTE_BASE?$ORDER_ID={$ORDER_ID}"

fun NavGraphBuilder.feedbackBottomSheet() {
    composable(
        route = FEEDBACK_ROUTE,
        arguments = listOf(navArgument(ORDER_ID) { type = NavType.IntType })
    ) {backStackEntry ->
        FeedbackSheet(
            orderID = backStackEntry.arguments?.getInt(ORDER_ID).or0()
        )
    }
}

fun NavController.navigateToFeedbackSheet(
    orderId: Int
) {
    val route = "$FEEDBACK_ROUTE_BASE?$ORDER_ID=$orderId"
    safeNavigate(
        route,
        navOptions {
            launchSingleTop = true
            restoreState = false
            popUpTo(0) { inclusive = true }
        }
    )
}