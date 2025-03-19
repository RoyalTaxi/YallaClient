package uz.yalla.client.feature.payment.add_employee.view

internal sealed interface AddEmployeeIntent {
    data object OnNavigateBack: AddEmployeeIntent
    data class SetNumber(val number: String): AddEmployeeIntent
    data class SetFullName(val fullName: String): AddEmployeeIntent
}