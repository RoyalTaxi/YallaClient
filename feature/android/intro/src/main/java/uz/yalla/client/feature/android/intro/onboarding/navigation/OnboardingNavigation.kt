package uz.yalla.client.feature.android.intro.onboarding.navigation


import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.intro.onboarding.view.OnboardingRoute

internal const val ONBOARDING_ROUTE = "onboarding_route"

internal fun NavGraphBuilder.onboardingScreen(
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

internal fun NavController.navigateToOnboardingScreen(navOptions: NavOptions? = null) =
    navigate(ONBOARDING_ROUTE, navOptions)