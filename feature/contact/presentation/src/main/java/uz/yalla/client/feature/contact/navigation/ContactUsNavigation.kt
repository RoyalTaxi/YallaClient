package uz.yalla.client.feature.contact.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.contact.view.ContactUsRoute

 const val CONTACT_US_ROUTE = "contact_us_route"

sealed interface FromContactUs {
    data object NavigateBack: FromContactUs
    data class ToWeb(val title: String, val url: String) : FromContactUs
}

fun NavGraphBuilder.contactUsScreen(
    fromContactUs: (FromContactUs) -> Unit
) {
    composable(route = CONTACT_US_ROUTE) {
        ContactUsRoute(fromContactUs)
    }
}

fun NavController.navigateToContactUsScreen() = safeNavigate(CONTACT_US_ROUTE)
