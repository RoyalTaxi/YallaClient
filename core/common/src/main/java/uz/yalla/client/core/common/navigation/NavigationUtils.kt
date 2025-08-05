package uz.yalla.client.core.common.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

fun AnimatedContentTransitionScope<NavBackStackEntry>.getExitTransition(
    fromMap: Boolean = true,
    isGoingBackToMap: Boolean = false,
    isBackNavigation: Boolean = false
): ExitTransition {
    val targetRoute = targetState.destination.route
    val slideDirection = when {
        // If we're going back to map (even if it's recreated), use End direction
        targetRoute?.contains("map") == true -> {
            AnimatedContentTransitionScope.SlideDirection.End
        }
        // If it's a back navigation (like going back from WebScreen to AboutAppScreen), use End direction
        isBackNavigation -> {
            AnimatedContentTransitionScope.SlideDirection.End
        }
        // Otherwise use Start direction for forward navigation
        else -> {
            AnimatedContentTransitionScope.SlideDirection.Start
        }
    }

    return slideOutOfContainer(
        slideDirection,
        tween(700)
    )
}
