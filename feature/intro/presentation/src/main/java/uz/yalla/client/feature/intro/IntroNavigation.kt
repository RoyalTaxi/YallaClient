package uz.yalla.client.feature.intro

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.intro.language.navigation.FromLanguage
import uz.yalla.client.feature.intro.language.navigation.LANGUAGE_ROUTE
import uz.yalla.client.feature.intro.language.navigation.languageScreen
import uz.yalla.client.feature.intro.notification_permission.navigation.navigateToNotificationPermissionScreen
import uz.yalla.client.feature.intro.notification_permission.navigation.notificationPermissionScreen
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
        languageScreen { fromLanguage ->
            when (fromLanguage) {
                FromLanguage.NavigateOnboarding -> navController.navigateToOnboardingScreen()
            }
        }

        onboardingScreen(
            onNext = navController::navigateToNotificationPermissionScreen,
            onJumpNext = navController::navigateToNotificationPermissionScreen
        )

        notificationPermissionScreen(
            onPermissionGranted = navController::navigateToPermissionScreen
        )

        permissionScreen(
            onPermissionGranted = onPermissionGranted
        )
    }
}

fun NavController.navigateToIntroModel(navOptions: NavOptions? = null) =
    safeNavigate(INTRO_ROUTE, navOptions)