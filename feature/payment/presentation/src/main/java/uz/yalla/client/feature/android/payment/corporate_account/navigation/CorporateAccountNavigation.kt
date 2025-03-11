package uz.yalla.client.feature.android.payment.corporate_account.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.android.payment.corporate_account.view.AddCompanyRoute

internal const val ADD_CORPORATE_ACCOUNT_ROUTE = "add_corporate_account_route"

internal fun NavGraphBuilder.corporateAccountScreen(
    onNavigateBack:() -> Unit,

) {
    composable(
        route = ADD_CORPORATE_ACCOUNT_ROUTE
    ) {
        AddCompanyRoute(
            onNavigateBack = onNavigateBack
        )
    }
}

internal fun NavController.navigateToCorporateAccountScreen() = safeNavigate(
    ADD_CORPORATE_ACCOUNT_ROUTE)