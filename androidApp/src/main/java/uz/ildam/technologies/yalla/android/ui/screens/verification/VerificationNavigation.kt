package uz.ildam.technologies.yalla.android.ui.screens.verification

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val NUMBER = "number"
const val EXPIRES_IN = "expires_in"
const val VERIFICATION_ROUTE_BASE = "route_route"
const val VERIFICATION_ROUTE =
    "$VERIFICATION_ROUTE_BASE?$NUMBER={$NUMBER}&$EXPIRES_IN={$EXPIRES_IN}"

fun NavGraphBuilder.verificationScreen(
    onBack: () -> Unit,
    onClientFound: () -> Unit,
    onClientNotFound: (String, String) -> Unit,
) {
    composable(
        route = VERIFICATION_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() },
        arguments = listOf(
            navArgument(NUMBER) { type = NavType.StringType },
            navArgument(EXPIRES_IN) { type = NavType.IntType }
        )
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
    navigate(route, navOptions)
}