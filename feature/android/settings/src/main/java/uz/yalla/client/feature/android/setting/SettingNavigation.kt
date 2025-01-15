package uz.yalla.client.feature.android.setting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import uz.yalla.client.feature.android.setting.settings.navigation.SETTINGS_ROUTE
import uz.yalla.client.feature.core.navigation.safeNavigate
import uz.yalla.client.feature.android.setting.settings.navigation.settingsScreen
import uz.yalla.client.feature.core.navigation.safePopBackStack

const val SETTING_MODULE_ROUTE = "settings_module_route"

fun NavGraphBuilder.settingsModule(
    navController: NavHostController
) {
    navigation(
        startDestination = SETTINGS_ROUTE,
        route = SETTING_MODULE_ROUTE
    ) {
        settingsScreen(
            onNavigateBack = navController::safePopBackStack
        )
    }
}

fun NavController.navigateToSettingModule() = safeNavigate(SETTING_MODULE_ROUTE)