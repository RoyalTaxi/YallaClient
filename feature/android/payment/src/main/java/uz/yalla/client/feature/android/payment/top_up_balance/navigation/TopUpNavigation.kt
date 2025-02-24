package uz.yalla.client.feature.android.payment.top_up_balance.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.payment.top_up_balance.view.TopUpRoute
import uz.yalla.client.feature.core.navigation.safeNavigate

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