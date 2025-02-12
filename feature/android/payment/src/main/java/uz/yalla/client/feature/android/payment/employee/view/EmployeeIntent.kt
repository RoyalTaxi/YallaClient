package uz.yalla.client.feature.android.payment.employee

import uz.yalla.client.feature.android.payment.business_account.view.BusinessAccountIntent

internal sealed interface EmployeeIntent {
    data object OnNavigateBack : EmployeeIntent
}