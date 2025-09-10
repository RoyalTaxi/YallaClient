package uz.yalla.client.feature.home.presentation.intent

sealed interface HomeEffect {
    data object EnableLocation : HomeEffect
    data object GrantLocation : HomeEffect
    data class NavigateToCancelled(val orderId: Int) : HomeEffect
    data object NavigateToRegister : HomeEffect
    data object NavigateToAddCard : HomeEffect
    data class ActiveOrderSheetState(val visible: Boolean): HomeEffect
}