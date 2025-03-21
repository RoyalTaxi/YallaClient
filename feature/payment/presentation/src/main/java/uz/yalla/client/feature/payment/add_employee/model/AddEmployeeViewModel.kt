package uz.yalla.client.feature.payment.add_employee.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class AddEmployeeViewModel : ViewModel() {

    private var _uiState = MutableStateFlow(AddEmployeeUIState())
    val uiState = _uiState.asStateFlow()

    fun setFullName(fullName: String) {
        _uiState.value = _uiState.value.copy(fullName = fullName)
    }

    fun setNumber(number: String) {
        _uiState.value = _uiState.value.copy(number = number)
    }
}