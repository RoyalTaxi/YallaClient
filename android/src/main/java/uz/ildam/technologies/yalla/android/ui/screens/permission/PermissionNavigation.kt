package uz.ildam.technologies.yalla.android.ui.screens.permission

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.ildam.technologies.yalla.android.navigation.safeNavigate

const val PERMISSION_ROUTE = "permission_route"

fun NavGraphBuilder.permissionScreen(
    onPermissionGranted: () -> Unit
) {
    composable(
        route = PERMISSION_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        PermissionRoute(onPermissionGranted = onPermissionGranted)
    }
}

fun NavController.navigateToPermissionScreen(navOptions: NavOptions? = null) =
    safeNavigate(PERMISSION_ROUTE, navOptions)