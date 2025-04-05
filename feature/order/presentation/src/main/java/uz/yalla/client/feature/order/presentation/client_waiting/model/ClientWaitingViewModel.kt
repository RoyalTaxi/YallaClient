package uz.yalla.client.feature.order.presentation.client_waiting.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.map.domain.model.request.GetRoutingDtoItem
import uz.yalla.client.feature.map.domain.usecase.GetRoutingUseCase
import uz.yalla.client.feature.order.domain.usecase.order.CancelRideUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingIntent
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingSheet.mutableIntentFlow

class ClientWaitingViewModel(
    private val cancelRideUseCase: CancelRideUseCase,
    private val getShowOrderUseCase: GetShowOrderUseCase,
    private val getRoutingUseCase: GetRoutingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientWaitingState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            uiState
                .distinctUntilChangedBy { it.driverRoute }
                .collectLatest { state ->
                    onIntent(ClientWaitingIntent.UpdateRoute(state.driverRoute))
                }
        }
    }

    fun setOrderId(orderId: Int) {
        _uiState.update { it.copy(orderId = orderId) }
        getOrderDetails()
    }

    fun onIntent(intent: ClientWaitingIntent) {
        viewModelScope.launch(Dispatchers.IO) {
            mutableIntentFlow.emit(intent)
        }
    }

    private fun getOrderDetails() {
        val orderId = uiState.value.orderId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            getShowOrderUseCase(orderId).onSuccess { order ->
                _uiState.update { it.copy(selectedOrder = order) }

                val clientPoint = order.taxi.routes.firstOrNull()?.coords?.let { coords ->
                    MapPoint(coords.lat, coords.lng)
                }

                val driverPoint = order.executor.coords.let { location ->
                    MapPoint(location.lat, location.lng)
                }

                if (clientPoint != null) {
                    getRouteFromDriverToClient(driverPoint, clientPoint)
                }
            }
        }
    }

    fun cancelRide() {
        val orderId = uiState.value.orderId
        viewModelScope.launch(Dispatchers.IO) {
            if (orderId != null) {
                cancelRideUseCase(orderId)
            }
        }.invokeOnCompletion {
            onIntent(ClientWaitingIntent.OnCancelled(uiState.value.orderId))
        }
    }

    private fun getRouteFromDriverToClient(driverPoint: MapPoint, clientPoint: MapPoint) {
        viewModelScope.launch(Dispatchers.IO) {
            val routingPoints = listOf(
                GetRoutingDtoItem(
                    type = GetRoutingDtoItem.START,
                    lat = driverPoint.lat,
                    lng = driverPoint.lng
                ),
                GetRoutingDtoItem(
                    type = GetRoutingDtoItem.END,
                    lat = clientPoint.lat,
                    lng = clientPoint.lng
                )
            )

            getRoutingUseCase(routingPoints).onSuccess { routeResponse ->
                val route = routeResponse.routing.map { point ->
                    MapPoint(point.lat, point.lng)
                }

                _uiState.update { it.copy(driverRoute = route) }
            }
        }
    }

    fun setDetailsBottomSheetVisibility(isVisible: Boolean) {
        _uiState.update { it.copy(detailsBottomSheetVisibility = isVisible) }
    }

    fun setCancelBottomSheetVisibility(isVisible: Boolean) {
        _uiState.update { it.copy(cancelBottomSheetVisibility = isVisible) }
    }

    fun clearState() {
        _uiState.update {
            it.copy(
                orderId = null,
                selectedOrder = null,
                driverRoute = emptyList()
            )
        }
    }
}