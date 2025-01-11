package uz.yalla.client.feature.android.auth.login.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.auth.login.view.LoginRoute
import uz.yalla.client.feature.core.navigation.safeNavigate


internal const val LOGIN_ROUTE = "login_route"

internal fun NavGraphBuilder.loginScreen(
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

internal fun NavController.navigateToLoginScreen(navOptions: NavOptions? = null) =
    safeNavigate(LOGIN_ROUTE, navOptions)