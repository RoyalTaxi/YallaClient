package uz.yalla.client.feature.history

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.core.presentation.navigation.safePopBackStack
import uz.yalla.client.feature.history.history.navigation.HISTORY_ROUTE
import uz.yalla.client.feature.history.history.navigation.historyScreen
import uz.yalla.client.feature.history.history_details.navigation.historyDetailsScreen
import uz.yalla.client.feature.history.history_details.navigation.navigateToDetailsScreen

internal const val HISTORY_MODULE_ROUTE = "history_module_route"

fun NavGraphBuilder.historyModule(
    onBack: () -> Unit,
    navController: NavHostController
) {
    navigation(
        startDestination = HISTORY_ROUTE,
        route = HISTORY_MODULE_ROUTE
    ) {
        historyScreen(
            onBack = onBack,
            onClickItem = navController::navigateToDetailsScreen
        )

        historyDetailsScreen(
            onNavigateBack = navController::safePopBackStack
        )
    }
}

fun NavController.navigateToHistoryModule() = safeNavigate(HISTORY_MODULE_ROUTE)