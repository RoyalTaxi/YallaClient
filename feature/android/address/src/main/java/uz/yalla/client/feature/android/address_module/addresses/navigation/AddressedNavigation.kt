package uz.yalla.client.feature.android.address_module.addresses.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.address_module.addresses.view.AddressesRoute
import uz.yalla.client.feature.core.navigation.safeNavigate

internal const val ADDRESSES_ROUTE = "addresses_route"

internal fun NavGraphBuilder.addressesScreen(
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