package uz.yalla.client.feature.android.intro.permission.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.intro.permission.view.PermissionRoute
import uz.yalla.client.feature.core.navigation.safeNavigate

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