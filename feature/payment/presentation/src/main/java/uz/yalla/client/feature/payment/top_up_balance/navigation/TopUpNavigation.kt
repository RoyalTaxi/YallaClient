package uz.yalla.client.feature.payment.top_up_balance.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.payment.top_up_balance.view.TopUpRoute

internal const val TOP_UP_ROUTE = "top_up_route"

internal fun NavGraphBuilder.topUpScreen(
    onNavigateBack: () -> Unit
){


    composable(
        route = TOP_UP_ROUTE
    ) {

        TopUpRoute(
            onNavigateBack = onNavigateBack
        )
    }
}

internal fun NavController.navigateToTopUpScreen() = safeNavigate(TOP_UP_ROUTE)