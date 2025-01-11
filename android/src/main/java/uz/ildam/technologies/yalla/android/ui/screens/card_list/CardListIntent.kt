package uz.ildam.technologies.yalla.android.ui.screens.card_list

sealed interface CardListIntent {
    data class SelectDefaultCard(val cardId: Int)
    data object AddNewCard : CardListIntent
    data object OnNavigateBack : CardListIntent
}