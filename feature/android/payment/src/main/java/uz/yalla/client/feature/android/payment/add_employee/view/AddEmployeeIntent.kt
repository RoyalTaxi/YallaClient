package uz.yalla.client.feature.android.payment.add_employee

internal sealed interface AddEmployeeIntent {
    data object OnNavigateBack: AddEmployeeIntent
    data class SetNumber(val number: String): AddEmployeeIntent
    data class SetFullName(val fullName: String): AddEmployeeIntent
}