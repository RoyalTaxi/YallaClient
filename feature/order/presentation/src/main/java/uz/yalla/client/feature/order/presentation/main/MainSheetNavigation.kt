package uz.yalla.client.feature.order.presentation.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.order.presentation.main.view.MainSheet

const val MAIN_SHEET_ROUTE = "main_sheet"

fun NavGraphBuilder.mainSheet() {
    composable(MAIN_SHEET_ROUTE) {
        MainSheet()
    }
}

fun NavController.navigateToMainSheet() = safeNavigate(
    MAIN_SHEET_ROUTE,
    navOptions {
        launchSingleTop = true
        restoreState = false
        popUpTo(0) { inclusive = false }
    }
)