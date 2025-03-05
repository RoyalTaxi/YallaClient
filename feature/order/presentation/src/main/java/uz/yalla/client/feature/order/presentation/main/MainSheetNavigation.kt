package uz.yalla.client.feature.order.presentation.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.order.presentation.main.view.MainSheet

const val MAIN_BOTTOM_SHEET_ROUTE = "main_bottom_sheet"

fun NavGraphBuilder.mainBottomSheet() {
    composable(MAIN_BOTTOM_SHEET_ROUTE) {
        MainSheet.View()
    }
}

fun NavController.navigateToMainBottomSheet() = safeNavigate(MAIN_BOTTOM_SHEET_ROUTE)