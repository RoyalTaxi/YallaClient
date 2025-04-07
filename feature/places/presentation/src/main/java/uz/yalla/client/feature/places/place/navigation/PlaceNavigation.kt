package uz.yalla.client.feature.places.place.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.places.place.view.AddressRoute
import uz.yalla.client.core.domain.model.type.PlaceType

internal const val ID = "id"
internal const val TYPE = "type"
internal const val ADDRESS_ROUTE_BASE = "address_route"
internal const val ADDRESS_ROUTE = "$ADDRESS_ROUTE_BASE?$ID={$ID}&$TYPE={$TYPE}"

internal fun NavGraphBuilder.addressScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = ADDRESS_ROUTE,
        arguments = listOf(
            navArgument(ID) {
                type = NavType.StringType // Use StringType to allow null
                nullable = true
                defaultValue = null
            },
            navArgument(TYPE) {
                type = NavType.StringType
                defaultValue = "other"
            }
        )
    ) { navBackStackEntry ->
        val id = navBackStackEntry.arguments?.getString(ID)?.toIntOrNull()
        val type = PlaceType.fromType(navBackStackEntry.arguments?.getString(TYPE).orEmpty())
        AddressRoute(
            id = id,
            type = type,
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToAddressScreen(
    type: String,
    id: Int? = null,
) {
    val route = buildString {
        append("$ADDRESS_ROUTE_BASE?$TYPE=$type")
        if (id != null) {
            append("&$ID=$id")
        }
    }
    safeNavigate(route)
}