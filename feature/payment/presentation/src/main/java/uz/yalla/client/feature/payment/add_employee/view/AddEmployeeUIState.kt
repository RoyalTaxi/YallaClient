package uz.yalla.client.feature.payment.add_employee.view

internal data class AddEmployeeUIState(
    val number: String = "",
    val fullName: String = ""
){
    val isAddButtonValid = number.isNotBlank() && fullName.isNotBlank()
}