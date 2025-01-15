package uz.yalla.client.feature.android.setting.settings.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.setting.settings.view.SettingsRoute
import uz.yalla.client.feature.core.navigation.safeNavigate

internal const val SETTINGS_ROUTE = "settings_route"

internal fun NavGraphBuilder.settingsScreen(
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
