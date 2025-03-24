package uz.yalla.client.feature.map.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
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
            navController.navigateToMainSheet(
                navOptions {
                    restoreState = false
                }
            )
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    var isNavigating by remember { mutableStateOf(false) }

    LaunchedEffect(currentBackStackEntry) {
        if (currentBackStackEntry == null && !isNavigating) {
            isNavigating = true
            coroutineScope.launch(Dispatchers.Main) {
                navController.navigateToMainSheet(
                    navOptions {
                        restoreState = false
                    }
                )
                isNavigating = false
            }
        }
    }

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { controller, _, _ ->
            val hasNoEntries = controller.currentBackStackEntry == null

            if (hasNoEntries && !isNavigating) {
                isNavigating = true
                coroutineScope.launch(Dispatchers.Main) {
                    controller.navigateToMainSheet(
                        navOptions {
                            restoreState = false
                        }
                    )
                    isNavigating = false
                }
            }
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
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