package uz.ildam.technologies.yalla.android.ui.screens.web

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.navigation.safeNavigate


const val TITLE = "title"
const val URL = "url"
const val WEB_ROUTE_BASE = "web_route"
const val WEB_ROUTE = "$WEB_ROUTE_BASE?$TITLE={$TITLE}&$URL={$URL}"

fun NavGraphBuilder.webScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = WEB_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() },
        arguments = listOf(
            navArgument(TITLE) { type = NavType.StringType },
            navArgument(URL) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        WebRoute(
            title = backStackEntry.arguments?.getString(TITLE).orEmpty(),
            url = backStackEntry.arguments?.getString(URL).orEmpty(),
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToWebScreen(
    title: String,
    url: String
) {
    val route = "$WEB_ROUTE_BASE?$TITLE=$title&$URL=$url"
    safeNavigate(route)
}