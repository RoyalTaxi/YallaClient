package uz.ildam.technologies.yalla.android.ui.screens.map

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.ildam.technologies.yalla.android.navigation.safeNavigate

const val MAP_ROUTE = "map_route"

fun NavGraphBuilder.mapScreen(
    onProfileClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onPaymentTypeClick: () -> Unit,
    onAddressesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPermissionDenied: () -> Unit,
    onAboutAppClick: () -> Unit,
    onContactUsClick: () -> Unit,
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
            onProfileClick = onProfileClick,
            onOrderHistoryClick = onOrderHistoryClick,
            onPaymentTypeClick = onPaymentTypeClick,
            onAddressesClick = onAddressesClick,
            onSettingsClick = onSettingsClick,
            onPermissionDenied = onPermissionDenied,
            onCancel = onCancel,
            onAddNewCard = onAddNewCard,
            onAboutAppClick = onAboutAppClick,
            onContactUsClick = onContactUsClick
        )
    }
}

fun NavController.navigateToMapScreen(navOptions: NavOptions? = null) =
    safeNavigate(MAP_ROUTE, navOptions)