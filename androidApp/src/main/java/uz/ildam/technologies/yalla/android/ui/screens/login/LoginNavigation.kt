package uz.ildam.technologies.yalla.android.ui.screens.login

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.ildam.technologies.yalla.android.navigation.safeNavigate

const val LOGIN_ROUTE = "login_route"

fun NavGraphBuilder.loginScreen(
    onBack: () -> Unit,
    onNext: (String, Int) -> Unit,
) {
    composable(
        route = LOGIN_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        LoginRoute(
            onBack = onBack,
            onNext = onNext
        )
    }
}

fun NavController.navigateToLoginScreen(navOptions: NavOptions? = null) =
    safeNavigate(LOGIN_ROUTE, navOptions)