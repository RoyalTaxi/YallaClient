package uz.yalla.client.feature.intro.language.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.intro.language.view.LanguageRoute

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