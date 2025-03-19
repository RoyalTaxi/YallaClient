package uz.yalla.client.feature.payment.business_account.view


internal sealed interface BusinessAccountIntent {
    data object OnNavigateBack : BusinessAccountIntent
    data object AddEmployee : BusinessAccountIntent
    data object OnClickEmployee : BusinessAccountIntent
    data object OnClickAddBalance : BusinessAccountIntent
}