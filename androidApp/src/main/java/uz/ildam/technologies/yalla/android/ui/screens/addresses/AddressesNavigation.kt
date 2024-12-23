package uz.ildam.technologies.yalla.android.ui.screens.addresses

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.ildam.technologies.yalla.android.navigation.safeNavigate

const val ADDRESSES_ROUTE = "addresses_route"

fun NavGraphBuilder.addressesScreen(
    onNavigateBack: () -> Unit,
    onClickAddress: (String, Int) -> Unit,
    onAddAddress: (String) -> Unit
) {
    composable(
        route = ADDRESSES_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() },
    ) {
        AddressesRoute(
            onNavigateBack = onNavigateBack,
            onClickAddress = onClickAddress,
            onAddAddress = onAddAddress
        )
    }
}

fun NavController.navigateToAddressesScreen() = safeNavigate(ADDRESSES_ROUTE)