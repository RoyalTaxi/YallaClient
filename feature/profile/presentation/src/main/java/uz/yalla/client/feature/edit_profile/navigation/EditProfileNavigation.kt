package uz.yalla.client.feature.edit_profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.profile.edit_profile.view.EditProfileRoute

internal const val EDIT_PROFILE_ROUTE = "edit_profile_route"

fun NavGraphBuilder.editProfileScreen(
    onNavigateToStart: () -> Unit,
    onNavigateBack: () -> Unit
) {
    composable(
        route = EDIT_PROFILE_ROUTE,
    ) {
        EditProfileRoute(
            onNavigateToStart = onNavigateToStart,
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToEditProfileScreen() = safeNavigate(EDIT_PROFILE_ROUTE)