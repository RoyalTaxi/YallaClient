package uz.yalla.client.feature.promocode.presentation.add_promocode.intent

sealed interface AddPromocodeIntent {
    data class UpdatePromoCode(val promoCode: String) : AddPromocodeIntent
    data object ActivatePromocode : AddPromocodeIntent
    data object NavigateBack : AddPromocodeIntent
    data object RetryAgain : AddPromocodeIntent
}