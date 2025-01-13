package uz.yalla.client.feature.android.payment.add_card.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.payment.add_card.view.AddCardRoute
import uz.yalla.client.feature.core.navigation.safeNavigate

internal const val ADD_CARD_ROUTE = "add_card_route"

internal fun NavGraphBuilder.addCardScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: (key: String, cardNumber: String, cardExpiry: String) -> Unit
) {
    composable(
        route = ADD_CARD_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        AddCardRoute(
            onNavigateBack = onNavigateBack,
            onNavigateNext = onNavigateNext
        )
    }
}

internal fun NavController.navigateToAddCardScreen() = safeNavigate(ADD_CARD_ROUTE)