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
    onNavigateBack: () -> Unit
) {
    composable(
        route = EDIT_PROFILE_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() },
    ) {
        EditProfileRoute(
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToEditProfileScreen() = safeNavigate(EDIT_PROFILE_ROUTE)