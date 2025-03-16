package uz.yalla.client.feature.registration.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.registration.presentation.view.RegistrationRoute

internal const val NUMBER = "number"
internal const val SECRET_KEY = "secret_key"
internal const val CREDENTIALS_ROUTE_BASE = "credentials_route"
internal const val CREDENTIALS_ROUTE =
    "$CREDENTIALS_ROUTE_BASE?$NUMBER={$NUMBER}&$SECRET_KEY={$SECRET_KEY}"

fun NavGraphBuilder.registrationScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    composable(
        route = CREDENTIALS_ROUTE,
        arguments = listOf(
            navArgument(NUMBER) { type = NavType.StringType },
            navArgument(SECRET_KEY) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        RegistrationRoute(
            number = backStackEntry.arguments?.getString(NUMBER).orEmpty(),
            secretKey = backStackEntry.arguments?.getString(SECRET_KEY).orEmpty(),
            onBack = onBack,
            onNext = onNext
        )
    }
}

fun NavController.navigateToRegistrationScreen(number: String, secretKey: String) {
    val route = "$CREDENTIALS_ROUTE_BASE?$NUMBER=$number&$SECRET_KEY=$secretKey"
    safeNavigate(route)
}