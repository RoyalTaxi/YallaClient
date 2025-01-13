package uz.yalla.client.feature.android.registration

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigation
import uz.yalla.client.feature.android.registration.credentials.navigation.CREDENTIALS_ROUTE
import uz.yalla.client.feature.android.registration.credentials.navigation.credentialsScreen
import uz.yalla.client.feature.android.registration.credentials.navigation.navigateToCredentialsScreen
import uz.yalla.client.feature.core.navigation.safePopBackStack


internal const val REGISTRATION_ROUTE = "registration_route"


fun NavGraphBuilder.registrationModule(
    navController: NavHostController,
    onNext: () -> Unit
) {
    navigation(
        startDestination = CREDENTIALS_ROUTE,
        route = REGISTRATION_ROUTE
    ) {
        credentialsScreen(
            onBack = navController::safePopBackStack,
            onNext = onNext,
        )
    }
}

fun NavController.navigateToRegistrationModule(number: String, secretKey: String) {
    navigateToCredentialsScreen(number, secretKey)
}
