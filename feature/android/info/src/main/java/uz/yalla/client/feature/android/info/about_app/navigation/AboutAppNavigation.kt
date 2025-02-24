package uz.yalla.client.feature.android.info.about_app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.info.about_app.view.AboutAppRoute
import uz.yalla.client.feature.core.navigation.safeNavigate


internal const val ABOUT_APP_ROUTE = "about_app_route"

internal fun NavGraphBuilder.aboutAppScreen(
    onBack: () -> Unit,
    onClickUrl: (String, String) -> Unit
) {
    composable(
        route = ABOUT_APP_ROUTE
    ) {
        AboutAppRoute(
            onClickUrl = onClickUrl,
            onNavigateBack = onBack
        )
    }
}

fun NavController.navigateToAboutAppScreen() = safeNavigate(ABOUT_APP_ROUTE)
