package uz.ildam.technologies.yalla.android.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import uz.ildam.technologies.yalla.android.ui.screens.credentials.credentialsScreen
import uz.ildam.technologies.yalla.android.ui.screens.credentials.navigateToCredentialsScreen
import uz.ildam.technologies.yalla.android.ui.screens.language.languageScreen
import uz.ildam.technologies.yalla.android.ui.screens.language.navigateToLanguageScreen
import uz.ildam.technologies.yalla.android.ui.screens.login.loginScreen
import uz.ildam.technologies.yalla.android.ui.screens.login.navigateToLoginScreen
import uz.ildam.technologies.yalla.android.ui.screens.map.mapScreen
import uz.ildam.technologies.yalla.android.ui.screens.map.navigateToMapScreen
import uz.ildam.technologies.yalla.android.ui.screens.onboarding.ONBOARDING_ROUTE
import uz.ildam.technologies.yalla.android.ui.screens.onboarding.onboardingScreen
import uz.ildam.technologies.yalla.android.ui.screens.verification.navigateToVerificationScreen
import uz.ildam.technologies.yalla.android.ui.screens.verification.verificationScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = ONBOARDING_ROUTE
        ) {
            onboardingScreen(
                onNext = navController::navigateToLanguageScreen
            )

            languageScreen(
                onBack = navController::safePopBackStack,
                onNext = navController::navigateToLoginScreen
            )

            loginScreen(
                onBack = navController::safePopBackStack,
                onNext = navController::navigateToVerificationScreen
            )

            credentialsScreen(
                onBack = navController::safePopBackStack,
                onNext = {
                    navController.navigateToMapScreen(
                        navOptions { popUpTo(0) { inclusive = true } }
                    )
                },
            )

            verificationScreen(
                onBack = navController::safePopBackStack,
                onClientFound = {
                    navController.navigateToMapScreen(
                        navOptions { popUpTo(0) { inclusive = true } }
                    )
                },
                onClientNotFound = navController::navigateToCredentialsScreen
            )

            mapScreen()
        }
    }
}