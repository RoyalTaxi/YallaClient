package uz.yalla.client.feature.profile.edit_profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.profile.edit_profile.view.EditProfileRoute

const val EDIT_PROFILE_ROUTE = "edit_profile_route"

sealed interface FromEditProfile {
    data object NavigateBack : FromEditProfile
    data object NavigateToLogin : FromEditProfile
}

fun NavGraphBuilder.editProfileScreen(
    fromEditProfile: (FromEditProfile) -> Unit
) {
    composable(
        route = EDIT_PROFILE_ROUTE
    ) {
        EditProfileRoute(fromEditProfile)
    }
}

fun NavController.navigateToEditProfileScreen() = safeNavigate(EDIT_PROFILE_ROUTE)
