package uz.yalla.client.feature.payment.business_account.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

internal class BusinessAccountViewModel(

) : ViewModel() {

    private val _uiState = MutableStateFlow(
        BusinessAccountUIState(
            overallBalance = "727 000 сум",
            employeeCount = "2",
            employees = listOf(
                EmployeeUIModel(
                    name = "Икромов Сардор",
                    phoneNumber = "+998991234567",
                    balance = "24 000 сум",
                    tripCount = "12 поездки"
                )
            )
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<BusinessAccountActionState>()
    val actionState = _actionState.asSharedFlow()
}