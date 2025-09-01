package uz.yalla.client.feature.promocode.presentation.add_promocode.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.promocode.presentation.add_promocode.view.AddPromocodeRoute

const val ADD_PROMOCODE_ROUTE = "add_promocode_route"

sealed interface FromAddPromocode {
    data object NavigateBack : FromAddPromocode
}

fun NavGraphBuilder.addPromocodeScreen(
    fromAddPromocode: (FromAddPromocode) -> Unit
) {
    composable(route = ADD_PROMOCODE_ROUTE) {
        AddPromocodeRoute(fromAddPromocode)
    }
}

fun NavController.navigateToAddPromocodeScreen() = safeNavigate(ADD_PROMOCODE_ROUTE)