package uz.yalla.client.feature.info.about_app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.info.about_app.view.AboutAppRoute


const val ABOUT_APP_ROUTE = "about_app_route"

sealed interface FromAboutApp {
    data object NavigateBack : FromAboutApp
    data class ToWeb(val title: String, val url: String) : FromAboutApp
}

fun NavGraphBuilder.aboutAppScreen(
    fromAboutApp: (FromAboutApp) -> Unit
) = composable(route = ABOUT_APP_ROUTE) { AboutAppRoute(fromAboutApp) }


fun NavController.navigateToAboutAppScreen() = safeNavigate(ABOUT_APP_ROUTE)
