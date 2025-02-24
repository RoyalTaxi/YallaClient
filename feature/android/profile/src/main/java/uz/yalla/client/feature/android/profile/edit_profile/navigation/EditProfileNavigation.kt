package uz.yalla.client.feature.android.profile.edit_profile.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.profile.edit_profile.view.EditProfileRoute
import uz.yalla.client.feature.core.navigation.safeNavigate

internal const val EDIT_PROFILE_ROUTE = "edit_profile_route"

internal fun NavGraphBuilder.editProfileScreen(
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