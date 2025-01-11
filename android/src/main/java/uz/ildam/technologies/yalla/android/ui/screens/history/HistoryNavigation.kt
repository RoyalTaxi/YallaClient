package uz.ildam.technologies.yalla.android.ui.screens.history

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.ildam.technologies.yalla.android.navigation.safeNavigate

const val HISTORY_ROUTE = "history_route"

fun NavGraphBuilder.historyScreen(
    onBack: () -> Unit,
    onClickItem: (Int) -> Unit,
) {
    composable(
        route = HISTORY_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        HistoryRoute(
            onBack = onBack,
            onClickItem = onClickItem
        )
    }
}

fun NavController.navigateToHistoryScreen(navOptions: NavOptions? = null) =
    safeNavigate(HISTORY_ROUTE, navOptions)