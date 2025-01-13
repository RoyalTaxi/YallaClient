package uz.yalla.client.feature.android.address_module

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import uz.yalla.client.feature.android.address_module.address.navigation.addressScreen
import uz.yalla.client.feature.android.address_module.address.navigation.navigateToAddressScreen
import uz.yalla.client.feature.android.address_module.addresses.navigation.ADDRESSES_ROUTE
import uz.yalla.client.feature.android.address_module.addresses.navigation.addressesScreen
import uz.yalla.client.feature.core.navigation.safeNavigate
import uz.yalla.client.feature.core.navigation.safePopBackStack

internal const val ADDRESS_MODULE_ROUTE = "address_module_route"

fun NavGraphBuilder.addressModule(
    navController: NavHostController
) {
    navigation(
        startDestination = ADDRESSES_ROUTE,
        route = ADDRESS_MODULE_ROUTE
    ) {
        addressesScreen(
            onNavigateBack = navController::safePopBackStack,
            onClickAddress = navController::navigateToAddressScreen,
            onAddAddress = navController::navigateToAddressScreen
        )

        addressScreen(
            onNavigateBack = navController::safePopBackStack
        )
    }
}

fun NavController.navigateToAddressModule() = safeNavigate(ADDRESS_MODULE_ROUTE)