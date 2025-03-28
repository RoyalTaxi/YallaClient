package uz.yalla.client.feature.order.presentation.no_service

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceSheet

const val NO_SERVICE_ROUTE = "no_service_bottom_sheet"

fun NavGraphBuilder.noServiceSheet() {
    composable(
        route = NO_SERVICE_ROUTE
    ) {
        NoServiceSheet.View()
    }
}

fun NavController.navigateToNoServiceSheet() {

    val route = NO_SERVICE_ROUTE
    safeNavigate(
        route,
        navOptions {
            launchSingleTop = true
            restoreState = false
        }
    )
}