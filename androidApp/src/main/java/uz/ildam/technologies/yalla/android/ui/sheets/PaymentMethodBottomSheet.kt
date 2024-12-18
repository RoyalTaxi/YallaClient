package uz.ildam.technologies.yalla.android.ui.sheets

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.components.item.SelectPaymentTypeItem
import uz.ildam.technologies.yalla.android.ui.screens.map.MapUIState
import uz.ildam.technologies.yalla.core.data.enums.PaymentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodBottomSheet(
    sheetState: SheetState,
    uiState: MapUIState,
    onSelectPaymentType: (PaymentType) -> Unit,
    onAddNewCard: () -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = YallaTheme.color.gray2,
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(YallaTheme.color.gray2)
                .navigationBarsPadding()
        ) {
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
            LazyColumn(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(YallaTheme.color.white)
            ) {
                item {
                    SelectPaymentTypeItem(
                        isSelected = uiState.selectedPaymentType == PaymentType.CASH,
                        tint = YallaTheme.color.gray,
                        painter = painterResource(R.drawable.ic_money),
                        text = stringResource(R.string.cash),
                        onSelect = { onSelectPaymentType(PaymentType.CASH) },
                    )
                }

                items(uiState.paymentTypes) { card ->
                    SelectPaymentTypeItem(
                        painter = painterResource(
                            id = when (card.cardId.length) {
                                16 -> R.drawable.img_logo_humo
                                32 -> R.drawable.img_logo_uzcard
                                else -> R.drawable.ic_money
                            }
                        ),
                        text = card.maskedPan,
                        isSelected = uiState.selectedPaymentType == PaymentType.CARD(
                            card.cardId,
                            cardNumber = card.maskedPan
                        ),
                        onSelect = {
                            onSelectPaymentType(
                                PaymentType.CARD(
                                    cardId = card.cardId,
                                    cardNumber = card.maskedPan
                                )
                            )
                        }
                    )
                }

                item {
                    SelectPaymentTypeItem(
                        isSelected = false,
                        tint = YallaTheme.color.gray,
                        painter = painterResource(R.drawable.ic_add),
                        text = stringResource(R.string.add_card),
                        onSelect = onAddNewCard,
                    )
                }
            }

            Box(
                modifier = Modifier.background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
            ) {
                YallaButton(
                    text = stringResource(R.string.ready),
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}