package uz.yalla.client.feature.web

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uz.yalla.client.core.presentation.navigation.safeNavigate


const val TITLE = "title"
const val URL = "url"
const val WEB_ROUTE_BASE = "web_route"
const val WEB_ROUTE = "$WEB_ROUTE_BASE?$TITLE={$TITLE}&$URL={$URL}"

fun NavGraphBuilder.webScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = WEB_ROUTE,
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