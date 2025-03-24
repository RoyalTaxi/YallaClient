package uz.yalla.client.feature.order.presentation.search.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.order.domain.usecase.order.CancelRideUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetSettingUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.order.domain.usecase.order.SearchCarUseCase
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheet.mutableIntentFlow
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetIntent
import kotlin.time.Duration.Companion.seconds

class SearchCarSheetViewModel(
    private val searchCarUseCase: SearchCarUseCase,
    private val getSettingUseCase: GetSettingUseCase,
    private val cancelRideUseCase: CancelRideUseCase,
    private val getShowOrderUseCase: GetShowOrderUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchCarSheetState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                searchCar()
                delay(5.seconds)
                yield()
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                getOrderDetails()
                delay(5.seconds)
                yield()
            }
        }
    }

    fun onIntent(intent: SearchCarSheetIntent) {
        viewModelScope.launch(Dispatchers.IO) {
            mutableIntentFlow.emit(intent)
        }
    }

    private fun searchCar() {
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

    private fun getOrderDetails() {
        val orderId = uiState.value.orderId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            getShowOrderUseCase(orderId).onSuccess { data ->
                _uiState.update { it.copy(order = data) }
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
                onIntent(SearchCarSheetIntent.OnCancelled(orderId))
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

    fun setOrderId(orderId: Int) {
        _uiState.update { it.copy(orderId = orderId) }
    }
}
