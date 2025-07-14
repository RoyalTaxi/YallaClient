package uz.yalla.client.feature.auth.verification.navigation

import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.auth.verification.view.VerificationRoute

internal const val NUMBER = "number"
internal const val EXPIRES_IN = "expires_in"
internal const val VERIFICATION_ROUTE_BASE = "verification_route"
internal const val VERIFICATION_ROUTE =
    "$VERIFICATION_ROUTE_BASE?$NUMBER={$NUMBER}&$EXPIRES_IN={$EXPIRES_IN}"

fun NavGraphBuilder.verificationScreen(
    onBack: () -> Unit,
    onClientFound: () -> Unit,
    onClientNotFound: (String, String) -> Unit,
) {
    composable(
        route = VERIFICATION_ROUTE,
        arguments = listOf(
            navArgument(NUMBER) { type = NavType.StringType },
            navArgument(EXPIRES_IN) { type = NavType.IntType }
        ),
        exitTransition = { fadeOut() },
        popExitTransition = { fadeOut() }
    ) { backStackEntry ->
        VerificationRoute(
            number = backStackEntry.arguments?.getString(NUMBER) ?: "",
            expiresIn = backStackEntry.arguments?.getInt(EXPIRES_IN) ?: 0,
            onBack = onBack,
            onClientFound = onClientFound,
            onClientNotFound = onClientNotFound
        )
    }
}

fun NavController.navigateToVerificationScreen(
    number: String,
    expiresIn: Int,
    navOptions: NavOptions? = null
) {
    val route = "$VERIFICATION_ROUTE_BASE?$NUMBER=$number&$EXPIRES_IN=$expiresIn"
    safeNavigate(route, navOptions)
}