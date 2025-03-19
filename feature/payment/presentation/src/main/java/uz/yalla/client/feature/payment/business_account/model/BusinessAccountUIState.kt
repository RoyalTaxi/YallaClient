package uz.yalla.client.feature.payment.business_account.model

internal data class BusinessAccountUIState(
    val overallBalance: String = "0 сум",
    val employeeCount: String = "0",
    val employees: List<EmployeeUIModel> = emptyList()
)

 internal data class EmployeeUIModel(
    val name: String,
    val phoneNumber: String,
    val balance: String,
    val tripCount: String
)