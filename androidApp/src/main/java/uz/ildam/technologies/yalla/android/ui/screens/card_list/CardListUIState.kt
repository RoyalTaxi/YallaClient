package uz.ildam.technologies.yalla.android.ui.screens.card_list

import uz.ildam.technologies.yalla.feature.payment.domain.model.CardListItemModel

data class CardListUIState(
    val cards: List<CardListItemModel> = emptyList(),
)