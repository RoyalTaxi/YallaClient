package uz.yalla.client.feature.android.history.history.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.history.history.view.HistoryRoute
import uz.yalla.client.feature.core.navigation.safeNavigate

const val HISTORY_ROUTE = "history_route"

fun NavGraphBuilder.historyScreen(
    onBack: () -> Unit,
    onClickItem: (Int) -> Unit,
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