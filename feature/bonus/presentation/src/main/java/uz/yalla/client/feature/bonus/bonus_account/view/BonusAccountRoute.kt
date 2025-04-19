package uz.yalla.client.feature.bonus.bonus_account.view

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.feature.bonus.bonus_account.model.BonusAccountViewModel

@Composable
internal fun BonusAccountRoute(
    onBack: () -> Unit,
    onBonusClicked: () -> Unit,
    vm: BonusAccountViewModel = koinViewModel()
) {
    BonusAccountScreen(
        onIntent = { intent ->
            when (intent) {
                is BonusAccountIntent.OnNavigateBack -> onBack()
                is BonusAccountIntent.OnBonusClicked -> onBonusClicked()
            }
        }
    )
}