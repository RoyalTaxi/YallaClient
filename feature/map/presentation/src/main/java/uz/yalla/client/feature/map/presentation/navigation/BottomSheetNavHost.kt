package uz.yalla.client.feature.map.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.yalla.client.feature.order.presentation.main.MAIN_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.main.mainSheet
import uz.yalla.client.feature.order.presentation.main.navigateToMainSheet
import uz.yalla.client.feature.order.presentation.search.searchForCarBottomSheet

@Composable
fun BottomSheetNavHost(
    modifier: Modifier,
    navController: NavHostController
) {
    LaunchedEffect(key1 = Unit) {
        launch(Dispatchers.Main) {
            navController.navigateToMainSheet()
        }
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MAIN_SHEET_ROUTE
    ) {
        mainSheet()
        searchForCarBottomSheet()
    }
}