package uz.yalla.client.feature.android.payment.corporate_account.view

internal sealed interface AddCompanyIntent {
    data class setCompanyName(val name: String) : AddCompanyIntent
    data class setCity(val city: String) : AddCompanyIntent
    data class setPersen(val person: String) : AddCompanyIntent
    data class setEmail(val email: String) : AddCompanyIntent
    data class setNumber(val number: String) : AddCompanyIntent
    data object sendData: AddCompanyIntent
    data object onNavigateBack: AddCompanyIntent
}