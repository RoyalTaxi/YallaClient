package uz.yalla.client.feature.intro

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.core.presentation.navigation.safePopBackStack
import uz.yalla.client.feature.intro.language.navigation.LANGUAGE_ROUTE
import uz.yalla.client.feature.intro.language.navigation.languageScreen
import uz.yalla.client.feature.intro.language.navigation.navigateToLanguageScreen
import uz.yalla.client.feature.intro.onboarding.navigation.navigateToOnboardingScreen
import uz.yalla.client.feature.intro.onboarding.navigation.onboardingScreen
import uz.yalla.client.feature.intro.permission.navigation.navigateToPermissionScreen
import uz.yalla.client.feature.intro.permission.navigation.permissionScreen

const val INTRO_ROUTE = "intro_route"

fun NavGraphBuilder.introModule(
    navController: NavHostController,
    onPermissionGranted: () -> Unit
) {
    navigation(
        startDestination = LANGUAGE_ROUTE,
        route = INTRO_ROUTE
    ) {
        languageScreen(
            onNext = navController::navigateToOnboardingScreen
        )

        onboardingScreen(
            onNext = navController::navigateToPermissionScreen,
            onJumpNext = navController::navigateToPermissionScreen
        )

        permissionScreen(
            onPermissionGranted = onPermissionGranted
        )
    }
}

fun NavController.navigateToIntroModel(navOptions: NavOptions? = null) =
    safeNavigate(INTRO_ROUTE, navOptions)