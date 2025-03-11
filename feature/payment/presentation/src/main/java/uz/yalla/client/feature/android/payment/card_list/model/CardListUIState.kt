package uz.yalla.client.feature.android.payment.card_list.model

import uz.yalla.client.core.data.enums.PaymentType
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.feature.payment.domain.model.CardListItemModel

internal data class CardListUIState(
    val cards: List<CardListItemModel> = emptyList(),
    val selectedPaymentType: PaymentType = AppPreferences.paymentType
)