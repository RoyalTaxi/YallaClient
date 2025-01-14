package uz.yalla.client.feature.android.payment.card_list.view

internal sealed interface CardListIntent {
    data class SelectDefaultCard(val cardId: Int)
    data object AddNewCard : CardListIntent
    data object OnNavigateBack : CardListIntent
}