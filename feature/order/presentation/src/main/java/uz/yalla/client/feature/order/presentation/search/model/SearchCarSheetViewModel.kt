package uz.yalla.client.feature.order.presentation.search.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.order.domain.usecase.order.CancelRideUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetSettingUseCase
import uz.yalla.client.feature.order.domain.usecase.order.SearchCarUseCase
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheet.mutableIntentFlow
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetIntent

class SearchCarSheetViewModel(
    private val searchCarUseCase: SearchCarUseCase,
    private val getSettingUseCase: GetSettingUseCase,
    private val cancelRideUseCase: CancelRideUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchCarSheetState())
    val uiState = _uiState.asStateFlow()

    fun onIntent(intent: SearchCarSheetIntent) {
        viewModelScope.launch(Dispatchers.IO) {
            mutableIntentFlow.emit(intent)
        }
    }

    fun searchCar() {
        val point = uiState.value.searchingAddressPoint ?: return
        val tariffId = uiState.value.tariffId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            searchCarUseCase(
                lat = point.lat,
                lng = point.lng,
                tariffId = tariffId
            ).onSuccess { cars ->
                onIntent(SearchCarSheetIntent.OnFoundCars(cars))
                _uiState.update { it.copy(cars = cars) }
            }
        }
    }

    fun getSetting() {
        viewModelScope.launch(Dispatchers.IO) {
            getSettingUseCase().onSuccess { setting ->
                _uiState.update { it.copy(setting = setting) }
            }
        }
    }

    fun cancelRide() {
        val orderId = uiState.value.orderId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            cancelRideUseCase(orderId).onSuccess {
                onIntent(SearchCarSheetIntent.OnCancelled)
            }
        }
    }

    fun setDetailsBottomSheetVisibility(isVisible: Boolean) {
        _uiState.update { it.copy(detailsBottomSheetVisibility = isVisible) }
    }

    fun setCancelBottomSheetVisibility(isVisible: Boolean) {
        _uiState.update { it.copy(cancelBottomSheetVisibility = isVisible) }
    }

    fun setPoint(point: MapPoint) {
        _uiState.update { it.copy(searchingAddressPoint = point) }
    }

    fun setTariffId(tariffId: Int) {
        _uiState.update { it.copy(tariffId = tariffId) }

    }
}
