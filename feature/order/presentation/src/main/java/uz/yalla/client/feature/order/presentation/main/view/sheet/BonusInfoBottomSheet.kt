package uz.yalla.client.feature.order.presentation.main.view.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import uz.yalla.client.core.common.button.EnableBonusButton
import uz.yalla.client.core.common.item.BonusBalanceItem
import uz.yalla.client.core.common.item.BonusInfoItem
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.PaymentMethodSheetIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BonusInfoBottomSheet(
    sheetState: SheetState,
    isBonusEnabled: Boolean,
    onDismissRequest: () -> Unit,
    onIntent: (PaymentMethodSheetIntent) -> Unit
) {
    val prefs = koinInject<AppPreferences>()
    val balance by prefs.balance.collectAsState(0)
    val minBonus by prefs.minBonus.collectAsState(0)

    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        contentColor = YallaTheme.color.white,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .background(YallaTheme.color.white)
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            BonusBalanceItem(
                balance = balance.toString(),
                onClickBalance = {}
            )

            EnableBonusButton(
                balance = balance,
                isBonusEnabled = isBonusEnabled,
                isBonusPaymentEnabled = balance > minBonus,
                onSwitchChecked = { isChecked ->
                    onIntent(
                        if (isChecked) PaymentMethodSheetIntent.EnableBonus
                        else PaymentMethodSheetIntent.DisableBonus
                    )
                },
                onClick = {
                    onIntent(PaymentMethodSheetIntent.EnableBonus)
                }
            )

            Text(
                text = stringResource(R.string.bonus),
                style = YallaTheme.font.title2,
                color = YallaTheme.color.black,
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 17.dp)
            )

            BonusInfoItem(
                percentage = 3,
                iconId = R.drawable.img_default_car,
                body = stringResource(R.string.bonus_body),
                onClick = {}
            )

            Text(
                text = stringResource(R.string.can_pay_with_bonus),
                style = YallaTheme.font.title2,
                color = YallaTheme.color.black,
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 17.dp)
            )

            BonusInfoItem(
                percentage = 50,
                iconId = R.drawable.ic_coin,
                body = stringResource(R.string.second_bonus_body),
                backgroundColor = YallaTheme.color.primary,
                textColor = YallaTheme.color.white,
                bodyTextColor = YallaTheme.color.white,
                onClick = {}
            )
        }
    }
}