package uz.yalla.client.feature.payment.card_list.model

import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.feature.payment.domain.model.CardListItemModel

 data class CardListUIState(
    val cards: List<CardListItemModel> = emptyList(),
    val selectedPaymentType: PaymentType = PaymentType.CASH,
    val isConfirmDeleteDialogVisibility: Boolean = false,
    val selectedCardId: String = "",
    val editCardEnabled: Boolean = false
)
