package uz.yalla.client.feature.bonus.bonus_account.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uz.yalla.client.core.common.navigation.getExitTransition
import uz.yalla.client.feature.bonus.bonus_account.view.BonusAccountRoute

internal const val BONUS_ACCOUNT_ROUTE = "bonus_account_route"

internal fun NavGraphBuilder.bonusAccountScreen(
    onBack: () -> Unit,
    onBonusClicked: () -> Unit
) {
    composable(
        route = BONUS_ACCOUNT_ROUTE,
        exitTransition = { getExitTransition(isGoingBackToMap = true) }
    ) {
        BackHandler { onBack() }
        BonusAccountRoute(
            onBack = onBack,
            onBonusClicked = onBonusClicked
        )
    }
}
