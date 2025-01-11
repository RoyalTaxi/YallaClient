package uz.ildam.technologies.yalla.android.ui.screens.card_list

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.ildam.technologies.yalla.android.navigation.safeNavigate

const val CARD_LIST_ROUTE = "card_list_route"

fun NavGraphBuilder.cardListScreen(
    onNavigateBack: () -> Unit,
    onAddNewCard: () -> Unit
) {
    composable(
        route = CARD_LIST_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        CardListRoute(
            onNavigateBack = onNavigateBack,
            onAddNewCard = onAddNewCard
        )
    }
}

fun NavController.navigateToCardListScreen() = safeNavigate(CARD_LIST_ROUTE)