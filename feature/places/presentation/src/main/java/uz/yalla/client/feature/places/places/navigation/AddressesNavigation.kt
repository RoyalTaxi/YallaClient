package uz.yalla.client.feature.places.places.navigation

import androidx.activity.compose.BackHandler
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.common.navigation.getExitTransition
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.places.places.view.AddressesRoute

internal const val ADDRESSES_ROUTE = "addresses_route"

internal fun NavGraphBuilder.addressesScreen(
    onBack: () -> Unit,
    onClickAddress: (String, Int) -> Unit,
    onAddAddress: (String) -> Unit
) {
    composable(
        route = ADDRESSES_ROUTE,
        exitTransition = { getExitTransition(isGoingBackToMap = true) }
    ) {
        BackHandler { onBack() }
        AddressesRoute(
            onNavigateBack = onBack,
            onClickAddress = onClickAddress,
            onAddAddress = onAddAddress
        )
    }
}

fun NavController.navigateToAddressesScreen() = safeNavigate(ADDRESSES_ROUTE)
