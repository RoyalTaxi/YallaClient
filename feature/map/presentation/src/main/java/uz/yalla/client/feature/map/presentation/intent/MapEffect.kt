package uz.yalla.client.feature.map.presentation.intent

sealed interface MapEffect {
    data object EnableLocation : MapEffect
    data object GrantLocation : MapEffect
    data class NavigateToCancelled(val orderId: Int) : MapEffect
    data object NavigateToRegister : MapEffect
    data object NavigateToAddCard : MapEffect
}