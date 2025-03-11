package uz.yalla.client.feature.map.presentation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.state.HamburgerButtonState
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.map.domain.model.request.GetRoutingDtoItem
import uz.yalla.client.feature.map.domain.usecase.GetAddressNameUseCase
import uz.yalla.client.feature.map.domain.usecase.GetPolygonUseCase
import uz.yalla.client.feature.map.domain.usecase.GetRoutingUseCase
import uz.yalla.client.feature.order.domain.model.response.PlaceNameModel
import uz.yalla.client.feature.order.domain.usecase.order.GetActiveOrdersUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.order.domain.usecase.tariff.GetTimeOutUseCase
import uz.yalla.client.feature.profile.domain.usecase.GetMeUseCase

class MapViewModel(
    private val getPolygonUseCase: GetPolygonUseCase,
    private val getAddressNameUseCase: GetAddressNameUseCase,
    private val getTimeOutUseCase: GetTimeOutUseCase,
    private val getShowOrderUseCase: GetShowOrderUseCase,
    private val getMeUseCase: GetMeUseCase,
    private val getActiveOrdersUseCase: GetActiveOrdersUseCase,
    private val getRoutingUseCase: GetRoutingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUIState())
    val uiState = _uiState.asStateFlow()

    init {
        getPolygon()
        getMe()
    }

    val hamburgerButtonState = uiState
        .distinctUntilChangedBy { it.selectedOrder }
        .map { state ->
            if (state.selectedOrder == null) HamburgerButtonState.OpenDrawer
            else HamburgerButtonState.NavigateBack
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = HamburgerButtonState.OpenDrawer
        )

    val isMapEnabled = uiState
        .distinctUntilChangedBy { it.selectedOrder }
        .map { OrderStatus.nonInteractive.contains(it.selectedOrder?.status).not() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = false
        )

    fun getPolygon() {
        viewModelScope.launch {
            getPolygonUseCase().onSuccess { polygon ->
                _uiState.update { it.copy(polygon = polygon) }
            }.onFailure {

            }
        }
    }

    fun getMe() {
        viewModelScope.launch {
            getMeUseCase().onSuccess { user -> _uiState.update { it.copy(user = user) } }
        }
    }

    suspend fun getAddressName(point: MapPoint): PlaceNameModel? = coroutineScope {
        val result = async { getAddressNameUseCase(point.lat, point.lng).getOrNull() }
        result.await()
    }

    fun getActiveOrders() {
        viewModelScope.launch {
            getActiveOrdersUseCase().onSuccess { activeOrders ->
                _uiState.update { it.copy(orders = activeOrders.list) }
            }
        }
    }

    fun getShowOrder() {
        val showingOrderId = uiState.value.showingOrderId ?: return
        viewModelScope.launch {
            getShowOrderUseCase(showingOrderId).onSuccess { order ->
                _uiState.update { it.copy(selectedOrder = order) }
            }
        }
    }

    fun getTimeout(point: MapPoint) {
        viewModelScope.launch {
            uiState.value.selectedTariffId?.let { tariffId ->
                getTimeOutUseCase(point.lat, point.lng, tariffId).onSuccess { timeout ->
                    _uiState.update { it.copy(timeout = timeout.timeout) }
                }
            }
        }
    }

    fun getRouting() {
        val points = listOfNotNull(
            uiState.value.selectedLocation?.point,
            *uiState.value.destinations.map { it.point }.toTypedArray()
        )

        if (points.size < 2) return

        viewModelScope.launch {
            val addresses = mutableListOf<GetRoutingDtoItem>()

            addresses.add(
                GetRoutingDtoItem(
                    type = GetRoutingDtoItem.START,
                    lat = points.first().lat,
                    lng = points.first().lng
                )
            )

            for (address in 1..<addresses.lastIndex) {
                GetRoutingDtoItem(
                    type = GetRoutingDtoItem.START,
                    lat = points[address].lat,
                    lng = points[address].lng
                )
            }

            addresses.add(
                GetRoutingDtoItem(
                    type = GetRoutingDtoItem.END,
                    lat = points.last().lat,
                    lng = points.last().lng
                )
            )

            getRoutingUseCase(addresses).onSuccess { route ->
                _uiState.update {
                    it.copy(
                        route = route.routing.map { routingPoint ->
                            MapPoint(routingPoint.lat, routingPoint.lng)
                        }
                    )
                }
            }
        }
    }

    fun updateState(state: MapUIState) {
        _uiState.update { state }
    }

    fun setStateToNotFound() {
        _uiState.update {
            it.copy(
                selectedLocation = null,
                selectedTariffId = null,
                timeout = null,

                )
        }
    }
}