package uz.ildam.technologies.yalla.android.ui.screens.details

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val ID = "id"
const val DETAILS_ROUTE_BASE = "credentials_route"
const val DETAILS_ROUTE = "$DETAILS_ROUTE_BASE?$ID={$ID}"

fun NavGraphBuilder.detailsScreen(
    onNavigateBack: () -> Unit,
) {
    composable(
        route = DETAILS_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() },
        arguments = listOf(navArgument(ID) { type = NavType.IntType })
    ) { backStackEntry ->
        DetailsRoute(
            orderId = backStackEntry.arguments?.getInt(ID) ?: 0,
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToDetailsScreen(orderId: Int, navOptions: NavOptions? = null) {
    val route = "$DETAILS_ROUTE_BASE?$ID=$orderId"
    navigate(route, navOptions)
}