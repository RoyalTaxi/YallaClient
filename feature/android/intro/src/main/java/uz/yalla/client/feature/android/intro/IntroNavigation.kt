package uz.yalla.client.feature.android.intro

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import uz.yalla.client.feature.android.intro.language.navigation.languageScreen
import uz.yalla.client.feature.android.intro.language.navigation.navigateToLanguageScreen
import uz.yalla.client.feature.android.intro.onboarding.navigation.ONBOARDING_ROUTE
import uz.yalla.client.feature.android.intro.onboarding.navigation.onboardingScreen
import uz.yalla.client.feature.android.intro.permission.navigation.navigateToPermissionScreen
import uz.yalla.client.feature.android.intro.permission.navigation.permissionScreen
import uz.yalla.client.feature.core.navigation.safeNavigate
import uz.yalla.client.feature.core.navigation.safePopBackStack

const val INTRO_ROUTE = "intro_route"

fun NavGraphBuilder.introModule(
    navController: NavHostController,
    onPermissionGranted: () -> Unit
) {
    navigation(
        startDestination = ONBOARDING_ROUTE,
        route = INTRO_ROUTE
    ) {
        onboardingScreen(
            onNext = navController::navigateToPermissionScreen,
            onJumpNext = navController::navigateToLanguageScreen
        )

        permissionScreen(
            onPermissionGranted = navController::navigateToLanguageScreen
        )

        languageScreen(
            onBack = navController::safePopBackStack,
            onNext = onPermissionGranted
        )
    }
}

fun NavController.navigateToIntroModel(navOptions: NavOptions? = null) =
    safeNavigate(INTRO_ROUTE, navOptions)