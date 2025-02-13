package uz.yalla.client.feature.android.payment.corporate_account.model

internal sealed interface CorporateAccountActionState {
    data object Loading: CorporateAccountActionState
    data object Error: CorporateAccountActionState
    data object Success: CorporateAccountActionState
}