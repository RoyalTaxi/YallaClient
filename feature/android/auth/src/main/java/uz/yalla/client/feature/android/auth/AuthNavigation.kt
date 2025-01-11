package uz.yalla.client.feature.android.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import uz.yalla.client.feature.android.auth.login.navigation.LOGIN_ROUTE
import uz.yalla.client.feature.android.auth.login.navigation.loginScreen
import uz.yalla.client.feature.android.auth.verification.navigation.navigateToVerificationScreen
import uz.yalla.client.feature.android.auth.verification.navigation.verificationScreen
import uz.yalla.client.feature.core.navigation.safeNavigate
import uz.yalla.client.feature.core.navigation.safePopBackStack

const val AUTH_ROUTE = "auth_route"

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

fun NavController.navigateToAuthModule() = safeNavigate(AUTH_ROUTE)