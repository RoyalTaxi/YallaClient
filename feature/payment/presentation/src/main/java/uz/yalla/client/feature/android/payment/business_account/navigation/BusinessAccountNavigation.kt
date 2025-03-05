package uz.yalla.client.feature.android.payment.business_account.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.feature.android.payment.business_account.view.BusinessAccountRoute
import uz.yalla.client.feature.core.navigation.safeNavigate

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