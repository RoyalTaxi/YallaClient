package uz.yalla.client.feature.order.presentation.no_service.model

import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapViewValue
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceIntent
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceSheet.mutableIntentFlow

class NoServiceViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(NoServiceState())
    val uiState = _uiState.asStateFlow()

    fun setSelectAddressVisibility(isVisible: Boolean) {
        _uiState.update { it.copy(setLocationSheetVisibility = isVisible) }
    }

    fun setSelectFromMapVisibility(value: SelectFromMapViewValue) {
        _uiState.update { it.copy(selectFromMapVisibility = value) }
    }

    fun onIntent(intent: NoServiceIntent) {
        viewModelScope.launch(Dispatchers.IO) {
            mutableIntentFlow.emit(intent)
        }
    }
}