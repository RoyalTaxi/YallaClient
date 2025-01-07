package uz.ildam.technologies.yalla.android.ui.screens.about_app

import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.ildam.technologies.yalla.android.navigation.safeNavigate


const val ABOUT_APP_ROUTE = "about_app_route"

fun NavGraphBuilder.aboutAppScreen(
    onBack: () -> Unit,
    onClickUrl: (String, String) -> Unit
) {
    composable(
        route = ABOUT_APP_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        AboutAppRoute(
            onClickUrl = onClickUrl,
            onNavigateBack = onBack
        )
    }
}

fun NavController.navigateToAboutAppScreen() = safeNavigate(ABOUT_APP_ROUTE)
