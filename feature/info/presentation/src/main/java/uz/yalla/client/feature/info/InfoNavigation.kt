package uz.yalla.client.feature.info

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.core.presentation.navigation.safePopBackStack
import uz.yalla.client.feature.info.about_app.navigation.ABOUT_APP_ROUTE
import uz.yalla.client.feature.info.about_app.navigation.aboutAppScreen

const val INFO_ROUTE = "info_route"

fun NavGraphBuilder.infoModule(
    navController: NavHostController,
    onClickUrl: (String, String) -> Unit
) {
    navigation(
        startDestination = ABOUT_APP_ROUTE,
        route = INFO_ROUTE
    ) {
        aboutAppScreen(
            onBack = navController::safePopBackStack,
            onClickUrl = onClickUrl
        )
    }
}

fun NavController.navigateToInfoModule() = safeNavigate(INFO_ROUTE)