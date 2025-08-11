package uz.yalla.client.feature.promocode.presentation.add_promocode.intent

sealed interface AddPromocodeSideEffect {
    data object NavigateBack : AddPromocodeSideEffect
}