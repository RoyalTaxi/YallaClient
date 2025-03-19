package uz.yalla.client.feature.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.core.presentation.navigation.safePopBackStack
import uz.yalla.client.feature.profile.edit_profile.navigation.EDIT_PROFILE_ROUTE
import uz.yalla.client.feature.profile.edit_profile.navigation.editProfileScreen

internal const val PROFILE_MODULE_ROUTE = "profile_module_route"

fun NavGraphBuilder.profileModule(
    navController: NavHostController,
    onNavigateToStart: () -> Unit,
) {
    navigation(
        startDestination = EDIT_PROFILE_ROUTE,
        route = PROFILE_MODULE_ROUTE
    ) {
        editProfileScreen(
            onNavigateToStart = onNavigateToStart,
            onNavigateBack = navController::safePopBackStack
        )
    }
}

fun NavController.navigateToProfileModule() = safeNavigate(PROFILE_MODULE_ROUTE)