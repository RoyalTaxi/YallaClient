package uz.ildam.technologies.yalla.android.ui.screens.settings

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.ildam.technologies.yalla.android.navigation.safeNavigate

const val SETTINGS_ROUTE = "settings_route"

fun NavGraphBuilder.settingsScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = SETTINGS_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() },
    ) {
        SettingsRoute(onNavigateBack)
    }
}

fun NavController.navigateToSettings() = safeNavigate(SETTINGS_ROUTE)
