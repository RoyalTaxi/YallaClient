package uz.yalla.client.feature.android.payment.card_list.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.payment.card_list.view.CardListRoute
import uz.yalla.client.feature.core.navigation.safeNavigate

internal const val CARD_LIST_ROUTE = "card_list_route"

internal fun NavGraphBuilder.cardListScreen(
    onNavigateBack: () -> Unit,
    onAddNewCard: () -> Unit,
    onAddCompany: () -> Unit,
    onAddBusinessAccount: () -> Unit
) {
    composable(
        route = CARD_LIST_ROUTE
    ) {
        CardListRoute(
            onNavigateBack = onNavigateBack,
            onAddNewCard = onAddNewCard,
            onAddCompany = onAddCompany,
            onAddBusinessAccount = onAddBusinessAccount
        )
    }
}

fun NavController.navigateToCardListScreen() = safeNavigate(CARD_LIST_ROUTE)