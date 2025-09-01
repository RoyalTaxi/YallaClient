package uz.yalla.client.feature.history

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.core.presentation.navigation.safePopBackStack
import uz.yalla.client.feature.history.history.navigation.FromHistory
import uz.yalla.client.feature.history.history.navigation.HISTORY_ROUTE
import uz.yalla.client.feature.history.history.navigation.historyScreen
import uz.yalla.client.feature.history.history_details.navigation.FromHistoryDetails
import uz.yalla.client.feature.history.history_details.navigation.historyDetailsScreen
import uz.yalla.client.feature.history.history_details.navigation.navigateToDetailsScreen

const val HISTORY_MODULE_ROUTE = "history_module_route"

fun NavGraphBuilder.historyModule(
    navController: NavHostController
) {
    navigation(
        startDestination = HISTORY_ROUTE,
        route = HISTORY_MODULE_ROUTE
    ) {
        historyScreen { fromHistory ->
            when (fromHistory) {
                FromHistory.NavigateBack -> navController.safePopBackStack()
                is FromHistory.NavigateToDetails -> navController.navigateToDetailsScreen(
                    orderId = fromHistory.id
                )
            }
        }

        historyDetailsScreen { fromHistoryDetails ->
            when(fromHistoryDetails) {
                FromHistoryDetails.NavigateBack -> navController.safePopBackStack()
            }
        }
    }
}

fun NavController.navigateToHistoryModule() = safeNavigate(HISTORY_MODULE_ROUTE)