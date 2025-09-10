package uz.yalla.client.feature.order.presentation.client_waiting.model

import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.home.domain.model.request.GetRoutingDtoItem
import uz.yalla.client.feature.home.domain.usecase.GetRoutingUseCase
import uz.yalla.client.feature.order.domain.usecase.order.CancelRideUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingSheetChannel
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingSheetIntent

class ClientWaitingViewModel(
    private val cancelRideUseCase: CancelRideUseCase,
    private val getShowOrderUseCase: GetShowOrderUseCase,
    private val getRoutingUseCase: GetRoutingUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ClientWaitingState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            uiState
                .distinctUntilChangedBy { it.driverRoute }
                .collectLatest { state ->
                    onIntent(ClientWaitingSheetIntent.UpdateRoute(state.driverRoute))
                }
        }
    }

    fun setOrderId(orderId: Int) {
        _uiState.update { it.copy(orderId = orderId) }
        getOrderDetails()
    }

    fun onIntent(intent: ClientWaitingSheetIntent) {
        viewModelScope.launch {
            when (intent) {
                is ClientWaitingSheetIntent.SetFooterHeight -> setFooterHeight(intent.height)
                is ClientWaitingSheetIntent.SetHeaderHeight -> setHeaderHeight(intent.height)
                else -> {
                    ClientWaitingSheetChannel.sendIntent(intent)
                }
            }
        }
    }

    private fun getOrderDetails() {
        val orderId = uiState.value.orderId ?: return
        _uiState.update { it.copy(isOrderCancellable = false) }

        viewModelScope.launch {
            getShowOrderUseCase(orderId).onSuccess { order ->
                _uiState.update {
                    it.copy(
                        selectedOrder = order,
                        isOrderCancellable = order.status in OrderStatus.cancellable
                    )
                }

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
        viewModelScope.launch {
            if (orderId != null) {
                cancelRideUseCase(orderId)
            }
        }.invokeOnCompletion {
            onIntent(ClientWaitingSheetIntent.OnCancelled(uiState.value.orderId))
        }
    }

    private fun getRouteFromDriverToClient(driverPoint: MapPoint, clientPoint: MapPoint) {
        viewModelScope.launch {
            val routingPoints = listOf(
                GetRoutingDtoItem(
                    type = GetRoutingDtoItem.START,
                    lat = driverPoint.lat,
                    lng = driverPoint.lng
                ),
                GetRoutingDtoItem(
                    type = GetRoutingDtoItem.STOP,
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
        if (isVisible) getOrderDetails()
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

    private fun setHeaderHeight(height: Dp) {
        _uiState.update { it.copy(headerHeight = height) }
    }

    private fun setFooterHeight(height: Dp) {
        _uiState.update { it.copy(footerHeight = height) }
    }
}