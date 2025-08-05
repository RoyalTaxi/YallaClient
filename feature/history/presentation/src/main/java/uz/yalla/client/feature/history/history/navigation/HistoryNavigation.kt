package uz.yalla.client.feature.history.history.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.core.common.navigation.getExitTransition
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.history.history.view.HistoryRoute

const val HISTORY_ROUTE = "history_route"

fun NavGraphBuilder.historyScreen(
    onBack: () -> Unit,
    onClickItem: (Int) -> Unit
) {
    composable(
        route = HISTORY_ROUTE,
        exitTransition = { getExitTransition(isGoingBackToMap = true) }

    ) {
        BackHandler { onBack() }
        HistoryRoute(
            onBack = onBack,
            onClickItem = onClickItem
        )
    }
}

fun NavController.navigateToHistoryScreen(navOptions: NavOptions? = null) =
    safeNavigate(HISTORY_ROUTE, navOptions)
