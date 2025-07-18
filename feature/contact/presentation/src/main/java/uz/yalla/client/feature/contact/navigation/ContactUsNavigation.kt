package uz.yalla.client.feature.contact.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.contact.view.ContactUsRoute

internal const val CONTACT_US_ROUTE = "contact_us_route"

fun NavGraphBuilder.contactUsScreen(
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
