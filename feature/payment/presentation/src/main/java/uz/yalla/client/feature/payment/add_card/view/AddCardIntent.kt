package uz.yalla.client.feature.payment.add_card.view

internal sealed interface AddCardIntent {
    data class SetCardNumber(val number: String) : AddCardIntent
    data class SetExpiryDate(val date: String) : AddCardIntent
    data object OnClickScanCard : AddCardIntent
    data object OnNavigateBack : AddCardIntent
    data object OnClickLinkCard : AddCardIntent
}