package uz.yalla.client.feature.history.history.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.history.history.view.HistoryRoute

const val HISTORY_ROUTE = "history_route"

sealed interface FromHistory {
    data object NavigateBack : FromHistory
    data class NavigateToDetails(val id: Int) : FromHistory
}

fun NavGraphBuilder.historyScreen(
    fromHistory: (FromHistory) -> Unit
) {
    composable(
        route = HISTORY_ROUTE
    ) {
        HistoryRoute(fromHistory)
    }
}

fun NavController.navigateToHistoryScreen(navOptions: NavOptions? = null) =
    safeNavigate(HISTORY_ROUTE, navOptions)
