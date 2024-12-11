package uz.ildam.technologies.yalla.android.ui.screens.card_list

sealed interface CardListActionState {
    data object Loading : CardListActionState
    data object Error : CardListActionState
    data object Success : CardListActionState
}