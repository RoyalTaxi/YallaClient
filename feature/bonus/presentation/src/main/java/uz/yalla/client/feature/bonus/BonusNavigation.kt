package uz.yalla.client.feature.bonus

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.core.presentation.navigation.safePopBackStack
import uz.yalla.client.feature.bonus.bonus_account.navigation.BONUS_ACCOUNT_ROUTE
import uz.yalla.client.feature.bonus.bonus_account.navigation.bonusAccountScreen
import uz.yalla.client.feature.bonus.bonus_balance.navigation.bonusBalanceScreen
import uz.yalla.client.feature.bonus.bonus_balance.navigation.navigateToBonusBalanceScreen

internal const val BONUS_MODULE_ROUTE = "bonus_module_route"

fun NavGraphBuilder.bonusModule(
    navController: NavController,
    onAddPromocodeClicked: () -> Unit,
) {
    navigation(
        startDestination = BONUS_ACCOUNT_ROUTE,
        route = BONUS_MODULE_ROUTE
    ) {
        bonusAccountScreen(
            onBack = navController::safePopBackStack,
            onBonusClicked = navController::navigateToBonusBalanceScreen,
            onAddPromocodeClicked = onAddPromocodeClicked
        )

        bonusBalanceScreen(
            onBack = navController::safePopBackStack
        )
    }
}

fun NavController.navigateToBonusModule() = safeNavigate(BONUS_MODULE_ROUTE)