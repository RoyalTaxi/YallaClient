package uz.yalla.client.feature.android.payment.business_account.model


internal sealed interface BusinessAccountActionState {
    data object Loading : BusinessAccountActionState
    data object Error : BusinessAccountActionState
    data object Success: BusinessAccountActionState
}