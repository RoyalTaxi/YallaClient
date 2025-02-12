package uz.yalla.client.feature.android.payment.business_account.view

import uz.yalla.client.feature.android.payment.add_card.view.AddCardIntent

internal sealed interface BusinessAccountIntent {
    data object OnNavigateBack : BusinessAccountIntent
    data object AddEmployee : BusinessAccountIntent
    data object OnClickEmployee : BusinessAccountIntent
    data object OnClickAddBalance : BusinessAccountIntent
}