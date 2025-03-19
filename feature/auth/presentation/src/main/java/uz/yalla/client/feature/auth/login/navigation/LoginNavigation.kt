package uz.yalla.client.feature.auth.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.auth.login.view.LoginRoute


internal const val LOGIN_ROUTE = "login_route"

internal fun NavGraphBuilder.loginScreen(
    onBack: () -> Unit,
    onNext: (String, Int) -> Unit,
) {
    composable(
        route = LOGIN_ROUTE
    ) {
        LoginRoute(
            onBack = onBack,
            onNext = onNext
        )
    }
}

internal fun NavController.navigateToLoginScreen(navOptions: NavOptions? = null) =
    safeNavigate(LOGIN_ROUTE, navOptions)