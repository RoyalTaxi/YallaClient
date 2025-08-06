package uz.yalla.client.feature.profile.edit_profile.navigation

import androidx.activity.compose.BackHandler
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.common.navigation.getExitTransition
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.profile.edit_profile.view.EditProfileRoute

const val EDIT_PROFILE_ROUTE = "edit_profile_route"

fun NavGraphBuilder.editProfileScreen(
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    composable(
        route = EDIT_PROFILE_ROUTE,
        exitTransition = { getExitTransition(isGoingBackToMap = true) }
    ) {
        BackHandler { onBack() }
        EditProfileRoute(
            onNavigateBack = onBack,
            onNavigateToLogin = onNavigateToLogin
        )
    }
}

fun NavController.navigateToEditProfileScreen() = safeNavigate(EDIT_PROFILE_ROUTE)
