package uz.yalla.client.feature.setting.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.setting.view.SettingsRoute

internal const val SETTINGS_ROUTE = "settings_route"

fun NavGraphBuilder.settingsScreen(
    onBack: () -> Unit
) {
    composable(
        route = SETTINGS_ROUTE
    ) {
        SettingsRoute(onBack = onBack)
    }
}

fun NavController.navigateToSettings() = safeNavigate(SETTINGS_ROUTE)
