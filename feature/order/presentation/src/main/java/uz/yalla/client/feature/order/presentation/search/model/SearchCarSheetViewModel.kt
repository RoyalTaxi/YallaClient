package uz.yalla.client.feature.order.presentation.search.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetIntent
import uz.yalla.client.feature.order.domain.usecase.order.CancelRideUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetSettingUseCase
import uz.yalla.client.feature.order.domain.usecase.order.SearchCarUseCase

class SearchCarSheetViewModel(
    private val searchCarUseCase: SearchCarUseCase,
    private val getSettingUseCase: GetSettingUseCase,
    private val cancelRideUseCase: CancelRideUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchCarSheetState())
    val uiState = _uiState.asStateFlow()

    private val _intentFlow = MutableSharedFlow<SearchCarSheetIntent>()
    val intentFlow = _intentFlow.asSharedFlow()

    fun onIntent(intent: SearchCarSheetIntent) = viewModelScope.launch {
        when (intent) {
            is SearchCarSheetIntent.ClickDetails -> setDetailsBottomSheetVisibility(true)
            else -> _intentFlow.emit(intent)
        }
    }

    fun searchCar() = viewModelScope.launch {
        searchCarUseCase()
    }

    fun setDetailsBottomSheetVisibility(isVisible: Boolean) {
        _uiState.update { it.copy(detailsBottomSheetVisibility = isVisible) }
    }
}
