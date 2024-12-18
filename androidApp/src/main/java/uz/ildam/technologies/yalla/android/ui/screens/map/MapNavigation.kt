package uz.ildam.technologies.yalla.android.ui.screens.map

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val MAP_ROUTE = "map_route"

fun NavGraphBuilder.mapScreen(
    onOrderHistoryClick: () -> Unit,
    onPaymentTypeClick: () -> Unit,
    onPermissionDenied: () -> Unit,
    onCancel: () -> Unit,
    onAddNewCard: () -> Unit,
) {
    composable(
        route = MAP_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        MapRoute(
            onOrderHistoryClick = onOrderHistoryClick,
            onPaymentTypeClick = onPaymentTypeClick,
            onPermissionDenied = onPermissionDenied,
            onCancel = onCancel,
            onAddNewCard = onAddNewCard
        )
    }
}

fun NavController.navigateToMapScreen(navOptions: NavOptions? = null) =
    navigate(MAP_ROUTE, navOptions)