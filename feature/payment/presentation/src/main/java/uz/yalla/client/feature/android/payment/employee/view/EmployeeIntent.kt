package uz.yalla.client.feature.android.payment.employee.view

internal sealed interface EmployeeIntent {
    data object OnNavigateBack : EmployeeIntent
    data object AddBalance: EmployeeIntent
}