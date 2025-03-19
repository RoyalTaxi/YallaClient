package uz.yalla.client.feature.payment.card_list.view

internal sealed interface CardListIntent {
    data class SelectDefaultCard(val cardId: Int)
    data object AddNewCard : CardListIntent
    data object OnNavigateBack : CardListIntent
    data object AddCorporateAccount : CardListIntent
    data object AddBusinessAccount : CardListIntent
}