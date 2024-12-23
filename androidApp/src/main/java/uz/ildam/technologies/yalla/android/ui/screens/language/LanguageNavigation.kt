package uz.ildam.technologies.yalla.android.ui.screens.language

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.ildam.technologies.yalla.android.navigation.safeNavigate

const val LANGUAGE_ROUTE = "language_route"

fun NavGraphBuilder.languageScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    composable(
        route = LANGUAGE_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        LanguageRoute(
            onBack = onBack,
            onNext = onNext
        )
    }
}

fun NavController.navigateToLanguageScreen(navOptions: NavOptions? = null) =
    safeNavigate(LANGUAGE_ROUTE, navOptions)