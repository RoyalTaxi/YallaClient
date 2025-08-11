package uz.yalla.client.feature.bonus.bonus_account.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.feature.bonus.bonus_account.view.BonusAccountRoute

internal const val BONUS_ACCOUNT_ROUTE = "bonus_account_route"

internal fun NavGraphBuilder.bonusAccountScreen(
    onBack: () -> Unit,
    onBonusClicked: () -> Unit,
    onAddPromocodeClicked: () -> Unit,
) {
    composable(
        route = BONUS_ACCOUNT_ROUTE
    ) {
        BonusAccountRoute(
            onBack = onBack,
            onBonusClicked = onBonusClicked,
            onAddPromocodeClicked = onAddPromocodeClicked
        )
    }
}
