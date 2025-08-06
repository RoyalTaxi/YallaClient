package uz.yalla.client.feature.profile.edit_profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.profile.edit_profile.view.EditProfileRoute

const val EDIT_PROFILE_ROUTE = "edit_profile_route"

fun NavGraphBuilder.editProfileScreen(
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    composable(
        route = EDIT_PROFILE_ROUTE
    ) {
        EditProfileRoute(
            onNavigateBack = onBack,
            onNavigateToLogin = onNavigateToLogin
        )
    }
}

fun NavController.navigateToEditProfileScreen() = safeNavigate(EDIT_PROFILE_ROUTE)
