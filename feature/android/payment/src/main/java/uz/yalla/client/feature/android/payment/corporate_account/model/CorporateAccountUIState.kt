package uz.yalla.client.feature.android.payment.corporate_account.model

internal data class CorporateAccountActionState(
    val name: String = "",
    val city: String = "",
    val contactPerson: String = "",
    val number: String = "",
    val email: String = "",
){
    val buttonState: Boolean
        get() = name.isNotBlank() && city.isNotBlank() && contactPerson.isNotBlank() && number.isNotBlank() && email.isNotBlank()
}