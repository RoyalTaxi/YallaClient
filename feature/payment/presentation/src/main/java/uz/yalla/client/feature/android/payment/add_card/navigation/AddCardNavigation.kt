package uz.yalla.client.feature.android.payment.add_card.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.android.payment.add_card.view.AddCardRoute

internal const val ADD_CARD_ROUTE = "add_card_route"

internal fun NavGraphBuilder.addCardScreen(
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

internal fun NavController.navigateToAddCardScreen() = safeNavigate(ADD_CARD_ROUTE)