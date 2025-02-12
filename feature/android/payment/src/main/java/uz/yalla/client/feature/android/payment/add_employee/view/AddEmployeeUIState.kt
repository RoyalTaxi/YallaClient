package uz.yalla.client.feature.android.payment.add_employee

internal data class AddEmployeeUIState(
    val number: String = "",
    val fullName: String = ""
){
    val isAddButtonValid = number.isNotBlank() && fullName.isNotBlank()
}