package uz.yalla.client.feature.android.payment.card_list.model

import uz.ildam.technologies.yalla.core.data.enums.PaymentType
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.feature.payment.domain.model.CardListItemModel

internal data class CardListUIState(
    val cards: List<CardListItemModel> = emptyList(),
    val selectedPaymentType: PaymentType = AppPreferences.paymentType
)