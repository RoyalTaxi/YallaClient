package uz.yalla.client.feature.auth.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.auth.login.view.LoginRoute


internal const val LOGIN_ROUTE = "login_route"

sealed interface FromLogin {
    data object ToBack : FromLogin
    data class ToVerification(val phoneNumber: String, val seconds: Int) : FromLogin
}

fun NavGraphBuilder.loginScreen(fromLogin: (FromLogin) -> Unit) =
    composable(LOGIN_ROUTE) { LoginRoute(navigate = fromLogin) }


fun NavController.navigateToLoginScreen(navOptions: NavOptions? = null) =
    safeNavigate(LOGIN_ROUTE, navOptions)