package uz.yalla.client.feature.android.payment.corporate_account.view

internal sealed interface CorporateAccountIntent {
    data class SetCompanyName(val name: String) : CorporateAccountIntent
    data class SetCity(val city: String) : CorporateAccountIntent
    data class SetPerson(val person: String) : CorporateAccountIntent
    data class SetEmail(val email: String) : CorporateAccountIntent
    data class SetNumber(val number: String) : CorporateAccountIntent

    data class SetIndex(val index: String) : CorporateAccountIntent
    data class SetStreet(val street: String) : CorporateAccountIntent
    data class SetHomeOffice(val homeOffice: String) : CorporateAccountIntent

    data class SetBankName(val bankName: String) : CorporateAccountIntent
    data class SetCurrentAccount(val currentAccount: String) : CorporateAccountIntent
    data class SetMFO(val mfo: String) : CorporateAccountIntent

    data object sendData: CorporateAccountIntent
}