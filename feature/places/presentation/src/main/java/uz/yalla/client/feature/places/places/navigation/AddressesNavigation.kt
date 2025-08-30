package uz.yalla.client.feature.places.places.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.places.places.view.AddressesRoute

internal const val ADDRESSES_ROUTE = "addresses_route"

sealed interface FromAddresses {
    data object NavigateBack : FromAddresses
    data class NavigateToAddress(val typeName: String, val id: Int) : FromAddresses
    data class AddAddress(val typeName: String) : FromAddresses
}

internal fun NavGraphBuilder.addressesScreen(
    fromAddresses: (FromAddresses) -> Unit
) {
    composable(
        route = ADDRESSES_ROUTE
    ) {
        AddressesRoute(fromAddresses)
    }
}

fun NavController.navigateToAddressesScreen() = safeNavigate(ADDRESSES_ROUTE)
