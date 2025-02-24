package uz.yalla.client.feature.android.intro.language.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.intro.language.view.LanguageRoute
import uz.yalla.client.feature.core.navigation.safeNavigate

internal const val LANGUAGE_ROUTE = "language_route"

internal fun NavGraphBuilder.languageScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    composable(
        route = LANGUAGE_ROUTE
    ) {
        LanguageRoute(
            onBack = onBack,
            onNext = onNext
        )
    }
}

fun NavController.navigateToLanguageScreen(navOptions: NavOptions? = null) =
    safeNavigate(LANGUAGE_ROUTE, navOptions)