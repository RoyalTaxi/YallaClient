package uz.yalla.client.feature.map.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.map.presentation.view.MapRoute

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
    becomeDriverClick: (String, String) -> Unit,
    inviteFriendClick: (String, String) -> Unit
) {
    composable(
        route = MAP_ROUTE
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
            onContactUsClick = onContactUsClick,
            onBecomeDriverClick = becomeDriverClick,
            onInviteFriendClick = inviteFriendClick
        )
    }
}

fun NavController.navigateToMapScreen(navOptions: NavOptions? = null) =
    safeNavigate(MAP_ROUTE, navOptions)