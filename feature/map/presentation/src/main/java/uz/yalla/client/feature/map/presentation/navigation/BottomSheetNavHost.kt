package uz.yalla.client.feature.map.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.yalla.client.feature.order.presentation.main.MAIN_BOTTOM_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.main.mainBottomSheet
import uz.yalla.client.feature.order.presentation.main.navigateToMainBottomSheet
import uz.yalla.client.feature.order.presentation.search.searchForCarBottomSheet

@Composable
fun BottomSheetNavHost(
    modifier: Modifier,
    navController: NavHostController,
) {
    LaunchedEffect(key1 = Unit) {
        launch(Dispatchers.Main) {
            navController.navigateToMainBottomSheet()
        }
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MAIN_BOTTOM_SHEET_ROUTE
    ) {
        mainBottomSheet()
        searchForCarBottomSheet()
    }
}