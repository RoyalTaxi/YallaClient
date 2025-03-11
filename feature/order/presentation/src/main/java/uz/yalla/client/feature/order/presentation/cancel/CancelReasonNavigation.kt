package uz.yalla.client.feature.order.presentation.cancel

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate

const val CANCEL_REASON_ROUTE = "cancel_reason_route"

fun NavGraphBuilder.cancelReasonScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = CANCEL_REASON_ROUTE
    ) {
        CancelReasonRoute(
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToCancelReasonScreen() = safeNavigate(CANCEL_REASON_ROUTE)