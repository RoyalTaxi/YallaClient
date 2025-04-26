package uz.yalla.client.feature.bonus.bonus_account.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.koinInject
import uz.yalla.client.core.domain.local.AppPreferences

@Composable
internal fun BonusAccountRoute(
    onBack: () -> Unit,
    onBonusClicked: () -> Unit,
    prefs: AppPreferences = koinInject()
) {
    val balance by prefs.balance.collectAsState(0)

    BonusAccountScreen(
        balance = balance,
        onIntent = { intent ->
            when (intent) {
                is BonusAccountIntent.OnNavigateBack -> onBack()
                is BonusAccountIntent.OnBonusClicked -> onBonusClicked()
            }
        }
    )
}