package uz.yalla.client.feature.order.presentation.main.view.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import uz.yalla.client.core.common.button.EnableBonusButton
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.item.SelectPaymentTypeItem
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.PaymentMethodSheetIntent
import uz.yalla.client.feature.payment.domain.model.CardListItemModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodBottomSheet(
    sheetState: SheetState,
    isBonusEnabled: Boolean,
    paymentTypes: List<CardListItemModel>,
    selectedPaymentType: PaymentType,
    onIntent: (PaymentMethodSheetIntent) -> Unit
) {
    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = YallaTheme.color.gray2,
        sheetState = sheetState,
        onDismissRequest = { onIntent(PaymentMethodSheetIntent.OnDismissRequest) },
        dragHandle = null
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(YallaTheme.color.gray2)
                .navigationBarsPadding()
        ) {
            PaymentMethodHeader()

            PaymentMethodContent(
                onIntent = onIntent,
                selectedPaymentType = selectedPaymentType,
                paymentTypes = paymentTypes,
                isBonusEnabled = isBonusEnabled
            )

            PaymentMethodFooter {
                onIntent(PaymentMethodSheetIntent.OnDismissRequest)
            }
        }
    }
}

@Composable
private fun PaymentMethodHeader() {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = YallaTheme.color.white,
                shape = RoundedCornerShape(30.dp)
            )
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.payment_method),
            style = YallaTheme.font.title,
            color = YallaTheme.color.black
        )

        Text(
            text = stringResource(R.string.choose_payment_method),
            style = YallaTheme.font.label,
            color = YallaTheme.color.gray
        )
    }
}

@Composable
private fun PaymentMethodContent(
    isBonusEnabled: Boolean,
    paymentTypes: List<CardListItemModel>,
    selectedPaymentType: PaymentType,
    onIntent: (PaymentMethodSheetIntent) -> Unit
) {
    val prefs = koinInject<AppPreferences>()
    val balance by prefs.balance.collectAsState(0)
    val minBonus by prefs.minBonus.collectAsState(0)
    val isCardEnabled: Boolean by prefs.isCardEnabled.collectAsState(false)

    LazyColumn(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(YallaTheme.color.white)
    ) {
        item {
            EnableBonusButton(
                balance = balance,
                isBonusEnabled = isBonusEnabled,
                isBonusPaymentEnabled = balance > minBonus,
                innerModifier = Modifier.padding(horizontal = 20.dp),
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
        }

        item {
            SelectPaymentTypeItem(
                isSelected = selectedPaymentType == PaymentType.CASH,
                painter = painterResource(R.drawable.ic_money_color),
                text = stringResource(R.string.cash),
                onSelect = {
                    onIntent(
                        PaymentMethodSheetIntent.OnSelectPaymentType(
                            PaymentType.CASH
                        )
                    )
                },
            )
        }

        items(paymentTypes) { card ->
            SelectPaymentTypeItem(
                enabled = isCardEnabled,
                painter = painterResource(
                    id = when (card.cardId.length) {
                        16 -> R.drawable.ic_humo
                        32 -> R.drawable.ic_uzcard
                        else -> R.drawable.ic_money_color
                    }
                ),
                text = card.maskedPan,
                isSelected = selectedPaymentType == PaymentType.CARD(
                    card.cardId,
                    cardNumber = card.maskedPan
                ),
                onSelect = {
                    onIntent(
                        PaymentMethodSheetIntent.OnSelectPaymentType(
                            PaymentType.CARD(
                                cardId = card.cardId,
                                cardNumber = card.maskedPan
                            )
                        )
                    )
                }
            )
        }

        if (isCardEnabled) item {
            SelectPaymentTypeItem(
                isSelected = false,
                tint = YallaTheme.color.gray,
                painter = painterResource(R.drawable.ic_add),
                text = stringResource(R.string.add_card),
                onSelect = {
                    onIntent(PaymentMethodSheetIntent.OnDismissRequest)
                    onIntent(PaymentMethodSheetIntent.OnAddNewCard)
                }
            )
        }
    }
}

@Composable
fun PaymentMethodFooter(
    onClickButton: () -> Unit
) {
    Box(
        modifier = Modifier.background(
            color = YallaTheme.color.white,
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
        )
    ) {
        PrimaryButton(
            text = stringResource(R.string.ready),
            onClick = onClickButton,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        )
    }
}