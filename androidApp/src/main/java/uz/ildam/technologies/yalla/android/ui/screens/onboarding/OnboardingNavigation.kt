package uz.ildam.technologies.yalla.android.ui.screens.onboarding

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val ONBOARDING_ROUTE = "onboarding_route"

fun NavGraphBuilder.onboardingScreen(
    onNext: () -> Unit
) {
    composable(
        route = ONBOARDING_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        OnboardingRoute(onNext = onNext)
    }
}

fun NavController.navigateToOnboardingScreen(navOptions: NavOptions? = null) =
    navigate(ONBOARDING_ROUTE, navOptions)