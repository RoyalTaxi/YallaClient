package uz.yalla.client.feature.map.presentation.new_version.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import org.koin.androidx.scope.ScopeActivity
import uz.yalla.client.core.common.maps.MapsViewModel
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
    data class ToBecomeDriver(val title: String, val url: String) : FromMap
    data class ToInviteFriend(val title: String, val url: String) : FromMap
}

fun NavGraphBuilder.mapScreen(
    networkState: Boolean,
    navigate: (FromMap) -> Unit,
) {
    composable(
        route = MAP_ROUTE,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() }
    ) {
        val activity = LocalActivity.current as ScopeActivity
        val mapsViewModel: MapsViewModel = remember { activity.scope.get<MapsViewModel>() }
        MRoute(
            networkState = networkState,
            onNavigate = navigate,
            mapsViewModel = mapsViewModel
        )
    }
}

fun NavController.navigateToMapScreen(navOptions: NavOptions? = null) = safeNavigate(
    screen = MAP_ROUTE,
    navOptions = navOptions ?: navOptions {
        restoreState = false
        launchSingleTop = true
        popUpTo(MAP_ROUTE) { inclusive = true }
    }
)