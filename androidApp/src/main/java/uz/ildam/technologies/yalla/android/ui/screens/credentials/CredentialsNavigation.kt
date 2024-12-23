package uz.ildam.technologies.yalla.android.ui.screens.credentials

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
import uz.ildam.technologies.yalla.android.navigation.safeNavigate

const val NUMBER = "number"
const val SECRET_KEY = "expires_in"
const val CREDENTIALS_ROUTE_BASE = "credentials_route"
const val CREDENTIALS_ROUTE =
    "$CREDENTIALS_ROUTE_BASE?$NUMBER={$NUMBER}&$SECRET_KEY={$SECRET_KEY}"

fun NavGraphBuilder.credentialsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    composable(
        route = CREDENTIALS_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() },
        arguments = listOf(
            navArgument(NUMBER) { type = NavType.StringType },
            navArgument(SECRET_KEY) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        CredentialsRoute(
            number = backStackEntry.arguments?.getString(NUMBER) ?: "",
            secretKey = backStackEntry.arguments?.getString(SECRET_KEY) ?: "",
            onBack = onBack,
            onNext = onNext
        )
    }
}

fun NavController.navigateToCredentialsScreen(
    number: String,
    secretKey: String,
    navOptions: NavOptions? = null
) {
    val route = "$CREDENTIALS_ROUTE_BASE?$NUMBER=$number&$SECRET_KEY=$secretKey"
    safeNavigate(route, navOptions)
}