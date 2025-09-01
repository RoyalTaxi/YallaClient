package uz.yalla.client.feature.intro.language.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.intro.language.view.LanguageRoute

internal const val LANGUAGE_ROUTE = "language_route"

sealed interface FromLanguage {
    data object NavigateOnboarding : FromLanguage
}

internal fun NavGraphBuilder.languageScreen(
    fromLanguage: (FromLanguage) -> Unit
) {
    composable(
        route = LANGUAGE_ROUTE
    ) {
        LanguageRoute(fromLanguage)
    }
}

fun NavController.navigateToLanguageScreen(navOptions: NavOptions? = null) =
    safeNavigate(LANGUAGE_ROUTE, navOptions)