package uz.yalla.client.feature.android.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import uz.yalla.client.feature.android.profile.edit_profile.navigation.EDIT_PROFILE_ROUTE
import uz.yalla.client.feature.android.profile.edit_profile.navigation.editProfileScreen
import uz.yalla.client.feature.core.navigation.safeNavigate
import uz.yalla.client.feature.core.navigation.safePopBackStack

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