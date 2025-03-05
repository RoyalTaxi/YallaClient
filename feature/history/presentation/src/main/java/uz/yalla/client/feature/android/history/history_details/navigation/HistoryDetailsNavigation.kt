package uz.yalla.client.feature.android.history.history_details.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.android.history.history_details.view.HistoryDetailsRoute

internal const val ID = "id"
internal const val DETAILS_ROUTE_BASE = "credentials_route"
internal const val DETAILS_ROUTE = "$DETAILS_ROUTE_BASE?$ID={$ID}"

internal fun NavGraphBuilder.historyDetailsScreen(
    onNavigateBack: () -> Unit,
) {
    composable(
        route = DETAILS_ROUTE,
        arguments = listOf(navArgument(ID) { type = NavType.IntType })
    ) { backStackEntry ->
        HistoryDetailsRoute(
            orderId = backStackEntry.arguments?.getInt(ID) ?: 0,
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToDetailsScreen(orderId: Int, navOptions: NavOptions? = null) {
    val route = "$DETAILS_ROUTE_BASE?$ID=$orderId"
    safeNavigate(route, navOptions)
}