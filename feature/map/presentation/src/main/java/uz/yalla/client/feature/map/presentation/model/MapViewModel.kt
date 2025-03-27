package uz.yalla.client.feature.map.presentation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.state.HamburgerButtonState
import uz.yalla.client.core.common.state.MoveCameraButtonState
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.domain.model.SelectedLocation
import uz.yalla.client.feature.map.domain.model.request.GetRoutingDtoItem
import uz.yalla.client.feature.map.domain.usecase.GetAddressNameUseCase
import uz.yalla.client.feature.map.domain.usecase.GetRoutingUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetActiveOrdersUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.order.presentation.main.view.MainSheet
import uz.yalla.client.feature.profile.domain.usecase.GetMeUseCase
import kotlin.time.Duration.Companion.seconds

class MapViewModel(
    private val getAddressNameUseCase: GetAddressNameUseCase,
    private val getShowOrderUseCase: GetShowOrderUseCase,
    private val getMeUseCase: GetMeUseCase,
    private val getRoutingUseCase: GetRoutingUseCase,
    private val getActiveOrdersUseCase: GetActiveOrdersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUIState())
    val uiState = _uiState.asStateFlow()

    init {
        getMe()

        val markerStateInputs = uiState
            .map { state ->
                Triple(
                    state.selectedLocation,
                    state.timeout,
                    state.selectedOrder?.status in OrderStatus.nonInteractive
                )
            }
            .distinctUntilChanged()

        val hasLocationChanged = uiState
            .map { state ->
                Pair(
                    state.selectedLocation,
                    state.destinations,
                )
            }
            .distinctUntilChanged()

        viewModelScope.launch(Dispatchers.IO) {
            uiState
                .distinctUntilChangedBy { it.showingOrderId }
                .collectLatest { state ->
                    while (state.showingOrderId != null) {
                        getShowOrder(state.showingOrderId)
                        delay(5.seconds)
                    }
                }
        }

        viewModelScope.launch(Dispatchers.Main) {
            uiState
                .distinctUntilChangedBy { it.selectedLocation }
                .collectLatest { state ->
                    state.selectedLocation?.let { location ->
                        MainSheet.setLocation(location)
                    }
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            uiState
                .distinctUntilChangedBy { it.destinations }
                .collectLatest { state ->
                    MainSheet.setDestination(state.destinations)
                }
        }

        viewModelScope.launch(Dispatchers.Main) {
            markerStateInputs.collectLatest { (selectedLocation, timeout, isNonInteractive) ->
                _uiState.update { currentState ->
                    currentState.copy(
                        markerState = when {
                            selectedLocation == null -> YallaMarkerState.LOADING
                            isNonInteractive -> YallaMarkerState.Searching
                            else -> YallaMarkerState.IDLE(
                                title = selectedLocation.name,
                                timeout = timeout
                            )
                        }
                    )
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            hasLocationChanged.collectLatest { (_, destination) ->
                if (destination.isNotEmpty()) getRouting()
                else _uiState.update { it.copy(route = emptyList()) }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                getActiveOrders()
                delay(5.seconds)
                yield()
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            uiState
                .distinctUntilChangedBy { it.route }
                .collectLatest { state ->
                    _uiState.update {
                        it.copy(
                            moveCameraButtonState = when {
                                state.route.isEmpty() -> MoveCameraButtonState.MyLocationView
                                else -> MoveCameraButtonState.MyRouteView
                            }
                        )
                    }
                }
        }
    }

    val hamburgerButtonState = uiState
        .distinctUntilChangedBy { Pair(it.selectedOrder, it.showingOrderId) }
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
        .map { state ->
            when {
                state.markerState == YallaMarkerState.Searching -> false
                state.showingOrderId != null -> true
                OrderStatus.nonInteractive.contains(state.selectedOrder?.status) -> false
                else -> true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = true
        )

    fun getMe() {
        viewModelScope.launch(Dispatchers.IO) {
            getMeUseCase().onSuccess { user ->
                _uiState.update {
                    it.copy(
                        user = user
                    )
                }
            }
        }
    }

    fun getAddressName(point: MapPoint) {
        viewModelScope.launch(Dispatchers.IO) {
            getAddressNameUseCase(point.lat, point.lng).onSuccess { data ->
                _uiState.update {
                    it.copy(
                        selectedLocation = SelectedLocation(
                            name = data.displayName,
                            point = point,
                            addressId = data.id
                        )
                    )
                }
            }
        }
    }

    private fun getShowOrder(showingOrderId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            getShowOrderUseCase(showingOrderId).onSuccess { order ->
                _uiState.update {
                    it.copy(
                        selectedOrder = order,
                        showingOrderId = order.id,
                        selectedLocation = SelectedLocation(
                            name = order.taxi.routes.firstOrNull()?.fullAddress,
                            addressId = null,
                            point = order.taxi.routes.firstOrNull()?.coords?.let { c ->
                                MapPoint(c.lat, c.lng)
                            }
                        ),
                        destinations = order.taxi.routes.drop(0).map { d ->
                            Destination(
                                name = d.fullAddress,
                                point = MapPoint(d.coords.lat, d.coords.lng)
                            )
                        }
                    )
                }
            }
        }
    }


    private fun getActiveOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            getActiveOrdersUseCase().onSuccess { activeOrders ->
                _uiState.update { it.copy(orders = activeOrders.list) }
            }
        }
    }

    private fun getRouting() {
        val points = listOfNotNull(
            uiState.value.selectedLocation?.point,
            *uiState.value.destinations.mapNotNull { it.point }.toTypedArray()
        )

        if (points.size < 2) {
            _uiState.update { it.copy(route = emptyList()) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val addresses = mutableListOf<GetRoutingDtoItem>()

            addresses.add(
                GetRoutingDtoItem(
                    type = GetRoutingDtoItem.START,
                    lat = points.first().lat,
                    lng = points.first().lng
                )
            )

            for (i in 1 until addresses.size - 1) {
                addresses.add(
                    GetRoutingDtoItem(
                        type = GetRoutingDtoItem.POINT,
                        lat = points[i].lat,
                        lng = points[i].lng
                    )
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
                timeout = null
            )
        }
    }
}