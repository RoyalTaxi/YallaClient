package uz.yalla.client.feature.payment.add_card.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.payment.add_card.view.AddCardRoute

 const val ADD_CARD_ROUTE = "add_card_route"

 fun NavGraphBuilder.addCardScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: (key: String, cardNumber: String, cardExpiry: String) -> Unit
) {
    composable(
        route = ADD_CARD_ROUTE
    ) {
        AddCardRoute(
            onNavigateBack = onNavigateBack,
            onNavigateNext = onNavigateNext
        )
    }
}

 fun NavController.navigateToAddCardScreen() = safeNavigate(ADD_CARD_ROUTE)