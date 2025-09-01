package uz.yalla.client.feature.places

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.core.presentation.navigation.safePopBackStack
import uz.yalla.client.feature.places.place.navigation.FromPlace
import uz.yalla.client.feature.places.place.navigation.addressScreen
import uz.yalla.client.feature.places.place.navigation.navigateToAddressScreen
import uz.yalla.client.feature.places.places.navigation.ADDRESSES_ROUTE
import uz.yalla.client.feature.places.places.navigation.FromAddresses
import uz.yalla.client.feature.places.places.navigation.addressesScreen

internal const val ADDRESS_MODULE_ROUTE = "address_module_route"

fun NavGraphBuilder.addressModule(
    navController: NavHostController
) {
    navigation(
        startDestination = ADDRESSES_ROUTE,
        route = ADDRESS_MODULE_ROUTE
    ) {
        addressesScreen { fromAddresses ->
            when (fromAddresses) {
                FromAddresses.NavigateBack -> navController.safePopBackStack()
                is FromAddresses.AddAddress -> {
                    navController.navigateToAddressScreen(type = fromAddresses.typeName)
                }
                is FromAddresses.NavigateToAddress -> {
                    navController.navigateToAddressScreen(
                        type = fromAddresses.typeName,
                        id = fromAddresses.id
                    )
                }
            }
        }

        addressScreen { fromPlace ->
            when (fromPlace) {
                FromPlace.NavigateBack -> navController.safePopBackStack()
            }
        }
    }
}

fun NavController.navigateToAddressModule() = safeNavigate(ADDRESS_MODULE_ROUTE)