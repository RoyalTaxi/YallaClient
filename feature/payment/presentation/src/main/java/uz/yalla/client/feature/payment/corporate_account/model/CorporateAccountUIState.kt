package uz.yalla.client.feature.payment.corporate_account.model

internal data class CorporateAccountUIState(
    val name: String = "",
    val city: String = "",
    val contactPerson: String = "",
    val number: String = "",
    val email: String = "",

    val index: String = "",
    val street: String = "",
    val homeOffice: String = "",

    val bankName: String = "",
    val currentAccount: String = "",
    val mfo: String = ""

){
    val isCompanyPageValid: Boolean get() = name.isNotBlank() && city.isNotBlank() && contactPerson.isNotBlank() && number.isNotBlank() && email.isNotBlank()
    val isLegalAddressPageValid: Boolean get() = index.isNotBlank() && street.isNotBlank() && homeOffice.isNotBlank()
    val isBankDetailsPageValid: Boolean get() = bankName.isNotBlank() && currentAccount.isNotBlank() && mfo.isNotBlank()
}