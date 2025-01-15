package uz.ildam.technologies.yalla.android.ui.screens.contact_us

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.ildam.technologies.yalla.android.navigation.safeNavigate

const val CONTACT_US_ROUTE = "contact_us_route"

fun NavGraphBuilder.contactUsScreen(
    onBack: () -> Unit,
    onClickUrl: (String, String) -> Unit
) {
    composable(
        route = CONTACT_US_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        ContactUsRoute(
            onClickUrl = onClickUrl,
            onNavigateBack = onBack
        )
    }
}

fun NavController.navigateToContactUsScreen() = safeNavigate(CONTACT_US_ROUTE)
