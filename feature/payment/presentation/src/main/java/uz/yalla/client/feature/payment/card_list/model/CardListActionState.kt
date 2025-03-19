package uz.yalla.client.feature.payment.card_list.model

internal sealed interface CardListActionState {
    data object Loading : CardListActionState
    data object Error : CardListActionState
    data object Success : CardListActionState
}