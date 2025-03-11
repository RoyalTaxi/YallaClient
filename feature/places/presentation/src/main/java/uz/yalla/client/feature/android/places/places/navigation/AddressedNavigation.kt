package uz.yalla.client.feature.android.places.places.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.android.places.places.view.AddressesRoute

internal const val ADDRESSES_ROUTE = "addresses_route"

internal fun NavGraphBuilder.addressesScreen(
    onNavigateBack: () -> Unit,
    onClickAddress: (String, Int) -> Unit,
    onAddAddress: (String) -> Unit
) {
    composable(
        route = ADDRESSES_ROUTE,
    ) {
        AddressesRoute(
            onNavigateBack = onNavigateBack,
            onClickAddress = onClickAddress,
            onAddAddress = onAddAddress
        )
    }
}

fun NavController.navigateToAddressesScreen() = safeNavigate(ADDRESSES_ROUTE)