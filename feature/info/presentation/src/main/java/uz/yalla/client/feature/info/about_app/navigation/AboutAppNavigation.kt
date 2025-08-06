package uz.yalla.client.feature.info.about_app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.info.about_app.view.AboutAppRoute


internal const val ABOUT_APP_ROUTE = "about_app_route"

fun NavGraphBuilder.aboutAppScreen(
    onBack: () -> Unit,
    onClickUrl: (String, String) -> Unit
) {
    composable(route = ABOUT_APP_ROUTE) {
        AboutAppRoute(
            onClickUrl = onClickUrl,
            onNavigateBack = onBack
        )
    }
}

fun NavController.navigateToAboutAppScreen() = safeNavigate(ABOUT_APP_ROUTE)
