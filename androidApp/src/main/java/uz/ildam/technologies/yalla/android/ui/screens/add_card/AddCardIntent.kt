package uz.ildam.technologies.yalla.android.ui.screens.add_card


sealed interface AddCardIntent {
    data class SetCardNumber(val number: String) : AddCardIntent
    data class SetExpiryDate(val date: String) : AddCardIntent
    data object OnClickScanCard : AddCardIntent
    data object OnNavigateBack : AddCardIntent
    data object OnClickLinkCard : AddCardIntent
}