package uz.yalla.client.feature.android.payment.corporate_account.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.payment.corporate_account.view.AddCompanyRoute
import uz.yalla.client.feature.core.navigation.safeNavigate

internal const val ADD_CORPORATE_ACCOUNT_ROUTE = "add_corporate_account_route"

internal fun NavGraphBuilder.corporateAccountScreen(
    onNavigateBack:() -> Unit,

) {
    composable(
        route = ADD_CORPORATE_ACCOUNT_ROUTE,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
    ) {
        AddCompanyRoute(
            onNavigateBack = onNavigateBack
        )
    }
}

internal fun NavController.navigateToCorporateAccountScreen() = safeNavigate(
    ADD_CORPORATE_ACCOUNT_ROUTE)