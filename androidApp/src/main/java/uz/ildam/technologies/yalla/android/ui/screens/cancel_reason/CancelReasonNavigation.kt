package uz.ildam.technologies.yalla.android.ui.screens.cancel_reason

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val CANCEL_REASON_ROUTE = "cancel_reason_route"

fun NavGraphBuilder.cancelReasonScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = CANCEL_REASON_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        CancelReasonRoute(
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToCancelReasonScreen() = navigate(CANCEL_REASON_ROUTE)