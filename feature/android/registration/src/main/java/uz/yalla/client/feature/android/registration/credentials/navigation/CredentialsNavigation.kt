package uz.yalla.client.feature.android.registration.credentials.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uz.yalla.client.feature.android.registration.credentials.view.CredentialsRoute
import uz.yalla.client.feature.core.navigation.safeNavigate

internal const val NUMBER = "number"
internal const val SECRET_KEY = "secret_key"
internal const val CREDENTIALS_ROUTE_BASE = "credentials_route"
internal const val CREDENTIALS_ROUTE =
    "$CREDENTIALS_ROUTE_BASE?$NUMBER={$NUMBER}&$SECRET_KEY={$SECRET_KEY}"

internal fun NavGraphBuilder.credentialsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
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
            number = backStackEntry.arguments?.getString(NUMBER).orEmpty(),
            secretKey = backStackEntry.arguments?.getString(SECRET_KEY).orEmpty(),
            onBack = onBack,
            onNext = onNext
        )
    }
}

internal fun NavController.navigateToCredentialsScreen(number: String, secretKey: String) {
    val route = "$CREDENTIALS_ROUTE_BASE?$NUMBER=$number&$SECRET_KEY=$secretKey"
    safeNavigate(route)
}