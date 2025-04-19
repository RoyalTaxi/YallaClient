package uz.yalla.client.feature.bonus.bonus_balance.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.presentation.navigation.safeNavigate
import uz.yalla.client.feature.bonus.bonus_balance.view.BonusBalanceIntent
import uz.yalla.client.feature.bonus.bonus_balance.view.BonusBalanceScreen

internal const val BONUS_BALANCE_ROUTE = "bonus_balance_route"

internal fun NavGraphBuilder.bonusBalanceScreen(
    onBack: () -> Unit
){
    composable(
        route = BONUS_BALANCE_ROUTE
    ) {
        BonusBalanceScreen(
            onIntent = {intent ->
                when (intent) {
                    BonusBalanceIntent.OnNavigateBack -> onBack()
                }
            }
        )
    }
}

fun NavController.navigateToBonusBalanceScreen() = safeNavigate(BONUS_BALANCE_ROUTE)