package uz.yalla.client.feature.payment.employee.view

 sealed interface EmployeeIntent {
    data object OnNavigateBack : EmployeeIntent
    data object AddBalance: EmployeeIntent
}