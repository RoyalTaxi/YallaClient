package uz.yalla.client.feature.android

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import uz.yalla.client.feature.android.contact.contact_us.navigation.CONTACT_US_ROUTE
import uz.yalla.client.feature.android.contact.contact_us.navigation.contactUsScreen
import uz.yalla.client.feature.core.navigation.safeNavigate
import uz.yalla.client.feature.core.navigation.safePopBackStack

internal const val CONTACT_ROUTE = "contact_route"

fun NavGraphBuilder.contactModule(
    navController: NavHostController,
    onClickUrl: (String, String) -> Unit
) {
    navigation(
        startDestination = CONTACT_US_ROUTE,
        route = CONTACT_ROUTE
    ){
        contactUsScreen(
            onBack = navController::safePopBackStack,
            onClickUrl = onClickUrl,
        )
    }
}

fun NavController.navigateToContactModule() = safeNavigate(CONTACT_ROUTE)