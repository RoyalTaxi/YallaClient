package uz.yalla.client.feature.android.payment.card_list.model

import uz.ildam.technologies.yalla.feature.payment.domain.model.CardListItemModel
import uz.yalla.client.core.data.enums.PaymentType
import uz.yalla.client.core.data.local.AppPreferences

internal data class CardListUIState(
    val cards: List<CardListItemModel> = emptyList(),
    val selectedPaymentType: PaymentType = AppPreferences.paymentType
)