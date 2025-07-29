package uz.yalla.client.feature.map.presentation.new_version.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.map.presentation.new_version.view.MRoute

const val MAP_ROUTE = "map_route"

sealed interface FromMap {
    data object ToRegister : FromMap
    data object ToProfile : FromMap
    data object ToOrderHistory : FromMap
    data object ToPaymentType : FromMap
    data object ToAddresses : FromMap
    data object ToSettings : FromMap
    data object ToAboutApp : FromMap
    data object ToContactUs : FromMap
    data object ToBonuses : FromMap
    data object ToAddNewCard : FromMap
    data object ToNotifications : FromMap
    data class ToCancel(val orderId: Int) : FromMap
    data class ToBecomeDriver(val title: String, val url: String) : FromMap
    data class ToInviteFriend(val title: String, val url: String) : FromMap
}

fun NavGraphBuilder.mapScreen(
    networkState: Boolean,
    navigate: (FromMap) -> Unit
) {
    composable(
        route = MAP_ROUTE,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() }
    ) {
        MRoute(
            networkState = networkState,
            onNavigate = navigate
        )
    }
}

fun NavController.navigateToMapScreen(navOptions: NavOptions? = null) =
    safeNavigate(MAP_ROUTE, navOptions)