package uz.yalla.client.feature.intro.onboarding.navigation


import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.intro.onboarding.view.OnboardingRoute

internal const val ONBOARDING_ROUTE = "onboarding_route"

internal fun NavGraphBuilder.onboardingScreen(
    onNext: () -> Unit,
    onJumpNext: () -> Unit
) {
    composable(
        route = ONBOARDING_ROUTE,
        enterTransition = {
            if (initialState.destination.route == null) {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            } else {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            }
        },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        OnboardingRoute(onNext = onNext, onJumpNext = onJumpNext)
    }
}

fun NavController.navigateToOnboardingScreen(navOptions: NavOptions? = null) =
    safeNavigate(ONBOARDING_ROUTE, navOptions)