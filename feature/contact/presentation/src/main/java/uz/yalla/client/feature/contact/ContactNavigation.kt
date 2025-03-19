package uz.yalla.client.feature.contact

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.core.presentation.navigation.safePopBackStack
import uz.yalla.client.feature.contact.contact_us.navigation.CONTACT_US_ROUTE
import uz.yalla.client.feature.contact.contact_us.navigation.contactUsScreen

internal const val CONTACT_ROUTE = "contact_route"

fun NavGraphBuilder.contactModule(
    navController: NavHostController,
    onClickUrl: (String, String) -> Unit
) {
    navigation(
        startDestination = CONTACT_US_ROUTE,
        route = CONTACT_ROUTE
    ) {
        contactUsScreen(
            onBack = navController::safePopBackStack,
            onClickUrl = onClickUrl,
        )
    }
}

fun NavController.navigateToContactModule() = safeNavigate(CONTACT_ROUTE)