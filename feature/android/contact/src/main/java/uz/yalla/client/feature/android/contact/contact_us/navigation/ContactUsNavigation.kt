package uz.yalla.client.feature.android.contact.contact_us.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.contact.contact_us.view.ContactUsRoute
import uz.yalla.client.feature.core.navigation.safeNavigate

internal const val CONTACT_US_ROUTE = "contact_us_route"

internal fun NavGraphBuilder.contactUsScreen(
    onBack: () -> Unit,
    onClickUrl: (String, String) -> Unit
) {
    composable(
        route = CONTACT_US_ROUTE
    ) {
        ContactUsRoute(
            onClickUrl = onClickUrl,
            onNavigateBack = onBack
        )
    }
}

fun NavController.navigateToContactUsScreen() = safeNavigate(CONTACT_US_ROUTE)
