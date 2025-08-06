package uz.yalla.client.feature.map.presentation.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.yalla.client.core.common.map.ConcreteGoogleMap
import uz.yalla.client.core.common.map.ConcreteLibreMap
import uz.yalla.client.core.common.map.MapStrategy
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.state.NavigationButtonState
import uz.yalla.client.core.common.state.CameraButtonState.FirstLocation
import uz.yalla.client.core.common.state.CameraButtonState.MyLocationView
import uz.yalla.client.core.common.state.CameraButtonState.MyRouteView
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.MapType
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.feature.domain.usecase.GetNotificationsCountUseCase
import uz.yalla.client.feature.map.domain.model.request.GetRoutingDtoItem
import uz.yalla.client.feature.map.domain.usecase.GetAddressNameUseCase
import uz.yalla.client.feature.map.domain.usecase.GetRoutingUseCase
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.order.domain.usecase.order.GetActiveOrdersUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetSettingUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import uz.yalla.client.feature.profile.domain.usecase.GetMeUseCase
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.ceil
import kotlin.time.Duration.Companion.seconds

class MapViewModel(
    private val getAddressNameUseCase: GetAddressNameUseCase,
    private val getShowOrderUseCase: GetShowOrderUseCase,
    private val getMeUseCase: GetMeUseCase,
    private val getRoutingUseCase: GetRoutingUseCase,
    private val getActiveOrdersUseCase: GetActiveOrdersUseCase,
    private val prefs: AppPreferences,
    private val staticPrefs: StaticPreferences,
    private val getNotificationsCountUseCase: GetNotificationsCountUseCase,
    private val getSettingUseCase: GetSettingUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(MapUIState())
    val uiState = _uiState.asStateFlow()

    private val pollingOrderId = MutableStateFlow<Int?>(null)
    private var pollingJob: Job? = null

    private val requestSequence = AtomicInteger(0)

    private var hasInjectedOnceInThisSession = false

    init {
        val markerStateInputs = uiState
            .map { state ->
                Triple(
                    state.location,
                    state.timeout,
                    state.selectedOrder?.status in OrderStatus.nonInteractive
                )
            }
            .distinctUntilChanged()

        val hasLocationChanged = uiState
            .map { state ->
                Pair(
                    state.location,
                    state.destinations,
                )
            }
            .distinctUntilChanged()

        viewModelScope.launch {
            pollingOrderId.collectLatest { orderId ->
                pollingJob?.cancel()

                orderId ?: return@collectLatest

                pollingJob = viewModelScope.launch(Dispatchers.IO) {
                    while (isActive) {
                        getShowOrder(orderId)
                        delay(5.seconds)

                        if (pollingOrderId.value != orderId) break
                    }
                }
            }
        }

        viewModelScope.launch(Dispatchers.Main) {
            uiState
                .distinctUntilChangedBy { it.location }
                .collectLatest { state ->
                    state.location?.let { location ->
                        MainSheetChannel.setLocation(location)
                    }
                }
        }

        viewModelScope.launch {
            uiState
                .distinctUntilChangedBy { it.destinations }
                .collectLatest { state ->
                    MainSheetChannel.setDestination(state.destinations)
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

        viewModelScope.launch {
            hasLocationChanged.collectLatest { (_, destination) ->
                if (destination.isNotEmpty()) getRouting()
                else _uiState.update { it.copy(route = emptyList()) }
            }
        }

        viewModelScope.launch {
            uiState
                .distinctUntilChangedBy { it.route }
                .collectLatest { state ->
                    _uiState.update {
                        it.copy(
                            cameraButtonState = when {
                                state.route.isEmpty() -> MyLocationView
                                else -> MyRouteView
                            }
                        )
                    }
                }
        }
    }

    val navigationButtonState = uiState
        .distinctUntilChangedBy { Pair(it.selectedOrder, it.destinations) }
        .map { state ->
            if (state.selectedOrder != null || state.destinations.isNotEmpty()) NavigationButtonState.NavigateBack
            else NavigationButtonState.OpenDrawer
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = NavigationButtonState.OpenDrawer
        )

    private fun MapType.toStrategyClass(): Class<out MapStrategy> {
        return when (this) {
            MapType.Google, MapType.Gis -> ConcreteGoogleMap::class.java
            MapType.Libre -> ConcreteLibreMap::class.java
        }
    }

    fun getMe() = viewModelScope.launch {
        getMeUseCase().onSuccess { user ->
            _uiState.update { it.copy(user = user) }
            prefs.setBalance(user.client.balance)
        }.onFailure(::handleException)
    }

    fun getNotificationsCount() = viewModelScope.launch {
        getNotificationsCountUseCase()
            .onSuccess { count ->
                _uiState.update { it.copy(notificationsCount = count) }
            }
    }

    fun getSettingConfig() = viewModelScope.launch {
        getSettingUseCase().onSuccess { setting ->
            prefs.setBonusEnabled(setting.isBonusEnabled)
            prefs.setMinBonus(setting.minBonus)
            prefs.setMaxBonus(setting.maxBonus)
        }
    }

    private fun getAddressName(point: MapPoint) {
        viewModelScope.launch {
            getAddressNameUseCase(point.lat, point.lng).onSuccess { data ->
                _uiState.update {
                    it.copy(
                        location = Location(
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
        val currentSequence = requestSequence.get()
        viewModelScope.launch {
            getShowOrderUseCase(showingOrderId).onSuccess { order ->
                if (pollingOrderId.value == showingOrderId && currentSequence == requestSequence.get()) {
                    withContext(Dispatchers.Main.immediate) {
                        _uiState.update {
                            it.copy(
                                selectedOrder = order,
                                selectedTariffId = order.taxi.tariffId,
                                location = Location(
                                    name = order.taxi.routes.firstOrNull()?.fullAddress,
                                    addressId = null,
                                    point = order.taxi.routes.firstOrNull()?.coords?.let { c ->
                                        MapPoint(c.lat, c.lng)
                                    }
                                ),
                                destinations = order.taxi.routes.drop(1).map { d ->
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
        }
    }

//    fun getActiveOrders() {
//        viewModelScope.launch {
//            val alreadyMarkedAsProcessed = staticPrefs.hasProcessedOrderOnEntry
//
//            getActiveOrdersUseCase().onSuccess { activeOrders ->
//                val shouldInject = activeOrders.list.size == 1 &&
//                        !alreadyMarkedAsProcessed &&
//                        !hasInjectedOnceInThisSession
//
//                if (shouldInject) {
//                    setSelectedOrder(activeOrders.list.first())
//                    staticPrefs.hasProcessedOrderOnEntry = true
//                    hasInjectedOnceInThisSession = true
//                } else if (activeOrders.list.size > 1 && !alreadyMarkedAsProcessed) {
//                    _uiState.update { it.copy(isActiveOrdersSheetVisibility = true) }
//                    staticPrefs.hasProcessedOrderOnEntry = true
//                    hasInjectedOnceInThisSession = true
//                }
//
//                _uiState.update { it.copy(orders = activeOrders.list) }
//            }
//        }
//    }


    private fun getRouting() {
        val points = listOfNotNull(
            uiState.value.location?.point,
            *uiState.value.destinations.mapNotNull { it.point }.toTypedArray()
        )

        if (points.size < 2) {
            _uiState.update { it.copy(route = emptyList()) }
            return
        }

        viewModelScope.launch {
            val addresses = mutableListOf<GetRoutingDtoItem>()

            addresses.add(
                GetRoutingDtoItem(
                    type = GetRoutingDtoItem.START,
                    lat = points.first().lat,
                    lng = points.first().lng
                )
            )

            for (i in 1 until points.lastIndex) {
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
                val duration = route.duration
                    .takeIf { it != 0.0 }
                    ?.div(60)
                _uiState.update { state ->
                    state.copy(
                        orderEndsInMinutes = duration?.let { ceil(it).toInt() },
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

    fun clearState() {
        pollingJob?.cancel()
        pollingOrderId.value = null
        requestSequence.incrementAndGet()

        _uiState.update {
            it.copy(
                selectedOrder = null,
                location = null,
                drivers = emptyList(),
                destinations = emptyList(),
                selectedTariffId = null,
                route = emptyList(),
                driverRoute = emptyList()
            )
        }
    }

    private fun setStateToNotFound() {
        _uiState.update {
            it.copy(
                location = null,
                selectedTariffId = null,
                timeout = null
            )
        }
    }

    fun setSelectedOrder(order: ShowOrderModel) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            _uiState.update { it.copy(selectedOrder = order) }

            pollingOrderId.value = order.id
        }
    }

    fun collectMarkerMovement(
        collectable: Flow<Triple<Boolean, Boolean, MapPoint>>
    ) = viewModelScope.launch {
        collectable.collectLatest { (isMarkerMoving, isByUser, mapPoint) ->
            if (uiState.value.destinations.isEmpty()) {
                when {
                    isMarkerMoving.not() && uiState.value.selectedOrder == null -> {
                        withContext(Dispatchers.IO) {
                            delay(300)
                            getAddressName(mapPoint)
                        }
                    }

                    uiState.value.route.isEmpty() && uiState.value.selectedOrder?.status == null && uiState.value.location != null -> {
                        setStateToNotFound()
                    }
                }
            }

            if (!isMarkerMoving) {
                when {
                    uiState.value.cameraButtonState == MyRouteView && isByUser -> {
                        _uiState.update { it.copy(cameraButtonState = MyRouteView) }
                    }

                    uiState.value.cameraButtonState == MyRouteView && !isByUser -> {
                        _uiState.update { it.copy(cameraButtonState = FirstLocation) }
                    }

                    uiState.value.cameraButtonState == FirstLocation -> {
                        _uiState.update { it.copy(cameraButtonState = MyRouteView) }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        pollingJob?.cancel()
        super.onCleared()
    }
}
