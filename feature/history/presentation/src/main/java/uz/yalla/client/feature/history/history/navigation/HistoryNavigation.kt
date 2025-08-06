package uz.yalla.client.feature.history.history.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.history.history.view.HistoryRoute

const val HISTORY_ROUTE = "history_route"

fun NavGraphBuilder.historyScreen(
    onBack: () -> Unit,
    onClickItem: (Int) -> Unit
) {
    composable(
        route = HISTORY_ROUTE
    ) {
        HistoryRoute(
            onBack = onBack,
            onClickItem = onClickItem
        )
    }
}

fun NavController.navigateToHistoryScreen(navOptions: NavOptions? = null) =
    safeNavigate(HISTORY_ROUTE, navOptions)
