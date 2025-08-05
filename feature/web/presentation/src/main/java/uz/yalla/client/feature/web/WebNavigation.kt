package uz.yalla.client.feature.web

import androidx.activity.compose.BackHandler
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uz.yalla.client.core.common.navigation.getExitTransition
import uz.yalla.client.core.presentation.navigation.safeNavigate


const val TITLE = "title"
const val URL = "url"
const val WEB_ROUTE_BASE = "web_route"
const val WEB_ROUTE = "$WEB_ROUTE_BASE?$TITLE={$TITLE}&$URL={$URL}"

fun NavGraphBuilder.webScreen(
    fromMap: Boolean,
    onBack: (Boolean) -> Unit
) {
    composable(
        route = WEB_ROUTE,
        exitTransition = { 
            // When navigating back from WebScreen, we want to use the End direction (slide right)
            // regardless of whether we're going back to the map or another screen
            getExitTransition(fromMap, isGoingBackToMap = fromMap, isBackNavigation = !fromMap)
        },
        arguments = listOf(
            navArgument(TITLE) { type = NavType.StringType },
            navArgument(URL) { type = NavType.StringType }
        ),
    ) { backStackEntry ->
        BackHandler { onBack(fromMap) }
        WebRoute(
            title = backStackEntry.arguments?.getString(TITLE).orEmpty(),
            url = backStackEntry.arguments?.getString(URL).orEmpty(),
            onBack = { onBack(fromMap) }
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
