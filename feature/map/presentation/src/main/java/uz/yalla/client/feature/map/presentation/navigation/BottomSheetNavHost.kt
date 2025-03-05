package uz.yalla.client.feature.map.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import uz.yalla.client.feature.order.presentation.main.MAIN_BOTTOM_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.main.mainBottomSheet

@Composable
fun BottomSheetNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MAIN_BOTTOM_SHEET_ROUTE
    ) {
        mainBottomSheet()
    }
}