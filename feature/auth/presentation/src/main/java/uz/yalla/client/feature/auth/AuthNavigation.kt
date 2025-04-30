package uz.yalla.client.feature.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.core.presentation.navigation.safePopBackStack
import uz.yalla.client.feature.auth.login.navigation.LOGIN_ROUTE
import uz.yalla.client.feature.auth.login.navigation.loginScreen
import uz.yalla.client.feature.auth.verification.navigation.navigateToVerificationScreen
import uz.yalla.client.feature.auth.verification.navigation.verificationScreen

internal const val AUTH_ROUTE = "auth_route"

fun NavGraphBuilder.authModule(
    navController: NavHostController,
    onClientFound: () -> Unit,
    onClientNotFound: (String, String) -> Unit,
) {
    navigation(
        startDestination = LOGIN_ROUTE,
        route = AUTH_ROUTE
    ) {
        loginScreen(
            onBack = navController::safePopBackStack,
            onNext = navController::navigateToVerificationScreen
        )

        verificationScreen(
            onBack = navController::safePopBackStack,
            onClientFound = onClientFound,
            onClientNotFound = onClientNotFound
        )
    }
}

fun NavController.navigateToAuthModule(navOptions: NavOptions? = null) = safeNavigate(
    screen = AUTH_ROUTE,
    navOptions = navOptions
)