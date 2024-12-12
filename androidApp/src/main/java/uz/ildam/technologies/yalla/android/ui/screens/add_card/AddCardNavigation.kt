package uz.ildam.technologies.yalla.android.ui.screens.add_card

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ADD_CARD_ROUTE = "add_card_route"

fun NavGraphBuilder.addCardScreen(
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

fun NavController.navigateToAddCardScreen() = navigate(ADD_CARD_ROUTE)