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
    composable(route = ONBOARDING_ROUTE) {
        OnboardingRoute(onNext = onNext, onJumpNext = onJumpNext)
    }
}

fun NavController.navigateToOnboardingScreen(navOptions: NavOptions? = null) =
    safeNavigate(ONBOARDING_ROUTE, navOptions)