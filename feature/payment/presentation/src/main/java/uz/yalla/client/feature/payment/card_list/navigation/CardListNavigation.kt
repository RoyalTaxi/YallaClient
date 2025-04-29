package uz.yalla.client.feature.payment.card_list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.payment.card_list.view.CardListRoute

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

fun NavController.navigateToCardListScreen(navOptions: NavOptions? = null) =
    safeNavigate(CARD_LIST_ROUTE, navOptions)