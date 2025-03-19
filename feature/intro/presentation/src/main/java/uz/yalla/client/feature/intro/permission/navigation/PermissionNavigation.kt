package uz.yalla.client.feature.intro.permission.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.intro.permission.view.PermissionRoute

internal const val PERMISSION_ROUTE = "permission_route"

internal fun NavGraphBuilder.permissionScreen(
    onPermissionGranted: () -> Unit
) {
    composable(
        route = PERMISSION_ROUTE
    ) {
        PermissionRoute(onPermissionGranted = onPermissionGranted)
    }
}

internal fun NavController.navigateToPermissionScreen(navOptions: NavOptions? = null) =
    safeNavigate(PERMISSION_ROUTE, navOptions)