package uz.yalla.client.feature.order.presentation.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.order.presentation.main.view.MainSheet

const val MAIN_SHEET_ROUTE = "main_sheet"

fun NavGraphBuilder.mainSheet() {
    composable(MAIN_SHEET_ROUTE) {
        MainSheet.View()
    }
}

fun NavController.navigateToMainSheet() = safeNavigate(MAIN_SHEET_ROUTE)