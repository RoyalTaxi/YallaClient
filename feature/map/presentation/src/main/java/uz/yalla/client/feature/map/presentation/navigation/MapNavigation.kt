package uz.yalla.client.feature.map.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.map.presentation.view.MapRoute

const val MAP_ROUTE = "map_route"

fun NavGraphBuilder.mapScreen(
    networkState: Boolean,
    onRegisterClick: () -> Unit,
    onProfileClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onPaymentTypeClick: () -> Unit,
    onAddressesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutAppClick: () -> Unit,
    onContactUsClick: () -> Unit,
    onClickBonuses: () -> Unit,
    onCancel: (Int) -> Unit,
    onAddNewCard: () -> Unit,
    onNotificationsClick: () -> Unit,
    onBecomeDriverClick: (String, String) -> Unit,
    onInviteFriendClick: (String, String) -> Unit
) {
    composable(
        route = MAP_ROUTE
    ) {
        MapRoute(
            networkState = networkState,
            onProfileClick = onProfileClick,
            onOrderHistoryClick = onOrderHistoryClick,
            onPaymentTypeClick = onPaymentTypeClick,
            onAddressesClick = onAddressesClick,
            onSettingsClick = onSettingsClick,
            onCancel = onCancel,
            onAddNewCard = onAddNewCard,
            onAboutAppClick = onAboutAppClick,
            onContactUsClick = onContactUsClick,
            onBecomeDriverClick = onBecomeDriverClick,
            onNotificationsClick = onNotificationsClick,
            onInviteFriendClick = onInviteFriendClick,
            onClickBonuses = onClickBonuses,
            onRegister = onRegisterClick
        )
    }
}

fun NavController.navigateToMapScreen(navOptions: NavOptions? = null) =
    safeNavigate(MAP_ROUTE, navOptions)