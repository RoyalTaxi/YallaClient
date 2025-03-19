package uz.yalla.client.feature.payment.corporate_account.model

internal sealed interface CorporateAccountActionState {
    data object Loading: CorporateAccountActionState
    data object Error: CorporateAccountActionState
    data object Success: CorporateAccountActionState
}