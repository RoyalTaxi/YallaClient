package uz.yalla.client.feature.map.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import uz.yalla.client.feature.order.presentation.main.MAIN_BOTTOM_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.main.mainBottomSheet
import uz.yalla.client.feature.order.presentation.search.searchForCarBottomSheet

@Composable
fun BottomSheetNavHost(
    modifier: Modifier,
    navController: NavHostController
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MAIN_BOTTOM_SHEET_ROUTE
    ) {
        mainBottomSheet()

        searchForCarBottomSheet()
    }
}