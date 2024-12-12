package uz.ildam.technologies.yalla.android.ui.screens.card_list

import uz.ildam.technologies.yalla.core.data.enums.PaymentType
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.feature.payment.domain.model.CardListItemModel

data class CardListUIState(
    val cards: List<CardListItemModel> = emptyList(),
    val selectedPaymentType: PaymentType = AppPreferences.paymentType
)