package uz.yalla.client.feature.android.places

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import uz.yalla.client.feature.android.places.place.navigation.addressScreen
import uz.yalla.client.feature.android.places.place.navigation.navigateToAddressScreen
import uz.yalla.client.feature.android.places.places.navigation.ADDRESSES_ROUTE
import uz.yalla.client.feature.android.places.places.navigation.addressesScreen
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