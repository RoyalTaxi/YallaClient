package uz.yalla.client.feature.payment.business_account.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.payment.business_account.view.BusinessAccountRoute

internal const val BUSINESS_ACCOUNT_ROUTE = "business_account_route"

internal fun NavGraphBuilder.businessAccountScreen(
    onNavigateBack: () -> Unit,
    onClickEmployee: () -> Unit,
    onClickAddBalance: () -> Unit,
    onClickAddEmployee: () -> Unit
) {
    composable(
        route = BUSINESS_ACCOUNT_ROUTE
    ) {

        BusinessAccountRoute(
            onNavigateBack = onNavigateBack,
            onAddEmployee = onClickAddEmployee,
            onClickEmployee = onClickEmployee,
            onClickAddBalance = onClickAddBalance,
        )
    }
}

internal fun NavController.navigateToBusinessAccount() = safeNavigate(BUSINESS_ACCOUNT_ROUTE)