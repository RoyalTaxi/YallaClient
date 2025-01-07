package uz.ildam.technologies.yalla.android.ui.screens.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.data.enums.PaymentType
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.ildam.technologies.yalla.core.domain.model.ExecutorModel
import uz.ildam.technologies.yalla.feature.map.domain.model.request.GetRoutingDtoItem
import uz.ildam.technologies.yalla.feature.map.domain.model.response.map.PolygonRemoteItem
import uz.ildam.technologies.yalla.feature.map.domain.model.response.map.SearchForAddressItemModel
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.GetAddressNameUseCase
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.GetPolygonUseCase
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.GetRoutingUseCase
import uz.ildam.technologies.yalla.feature.order.domain.model.request.OrderTaxiDto
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderStatus
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.SettingModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.ShowOrderModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.ildam.technologies.yalla.feature.order.domain.usecase.order.CancelRideUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.order.GetActiveOrdersUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.order.GetSettingUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.order.OrderTaxiUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.order.RateTheRideUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.order.SearchCarUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff.GetTariffsUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff.GetTimeOutUseCase
import uz.ildam.technologies.yalla.feature.payment.domain.model.CardListItemModel
import uz.ildam.technologies.yalla.feature.payment.domain.usecase.GetCardListUseCase
import uz.ildam.technologies.yalla.feature.profile.domain.model.response.GetMeModel
import uz.ildam.technologies.yalla.feature.profile.domain.usecase.GetMeUseCase
import kotlin.time.Duration.Companion.seconds

class MapViewModel(
    private val getPolygonUseCase: GetPolygonUseCase,
    private val getAddressNameUseCase: GetAddressNameUseCase,
    private val getTariffsUseCase: GetTariffsUseCase,
    private val getTimeOutUseCase: GetTimeOutUseCase,
    private val orderTaxiUseCase: OrderTaxiUseCase,
    private val searchCarUseCase: SearchCarUseCase,
    private val getSettingUseCase: GetSettingUseCase,
    private val cancelRideUseCase: CancelRideUseCase,
    private val getCardListUseCase: GetCardListUseCase,
    private val getShowOrderUseCase: GetShowOrderUseCase,
    private val rateTheRideUseCase: RateTheRideUseCase,
    private val getMeUseCase: GetMeUseCase,
    private val getActiveOrdersUseCase: GetActiveOrdersUseCase,
    private val getRoutingUseCase: GetRoutingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<MapActionState>()
    val actionState = _actionState.asSharedFlow()

    private var addresses = listOf<PolygonRemoteItem>()

    init {
        fetchPolygons()
    }

    private fun fetchPolygons() = viewModelScope.launch {
        _actionState.emit(MapActionState.LoadingPolygon)
        getPolygonUseCase()
            .onSuccess { result ->
                addresses = result
                _actionState.emit(MapActionState.PolygonLoaded)
            }
            .onFailure {
                setNotFoundState()
            }
    }

    fun getAddressDetails(point: MapPoint) = viewModelScope.launch {
        if (point.lat == 0.0 || point.lng == 0.0) return@launch
        _actionState.emit(MapActionState.LoadingAddressId)
        if (addresses.isEmpty()) fetchPolygons()

        var addressId: Int? = null
        addresses.firstOrNull {
            val (inside, id) = isPointInsidePolygon(point)
            if (inside) {
                addressId = id
            }
            inside
        }

        setSelectedLocation(
            addressId = addressId,
            point = if (addressId != null) point else null
        )

        if (addressId != null) {
            _actionState.emit(MapActionState.AddressIdLoaded(addressId!!.toLong()))
        }

        fetchAddressName(point)
    }

    private fun fetchAddressName(point: MapPoint) = viewModelScope.launch {
        _actionState.emit(MapActionState.LoadingAddressName)
        getAddressNameUseCase(point.lat, point.lng)
            .onSuccess { result -> _actionState.emit(MapActionState.AddressNameLoaded(result.displayName)) }
            .onFailure { setNotFoundState() }
    }

    fun getTimeout(point: MapPoint) = viewModelScope.launch {
        val selectedTariff =
            uiState.value.selectedTariff ?: uiState.value.tariffs?.tariff?.firstOrNull()
        if (selectedTariff == null) return@launch

        _actionState.emit(MapActionState.LoadingTariffs)
        getTimeOutUseCase(point.lat, point.lng, selectedTariff.category.id.toInt())
            .onSuccess { result ->
                setTimeout(result.timeout)
                setDrivers(result.executors)
            }.onFailure {
                setTimeout(null)
                setDrivers(emptyList())
            }
    }

    fun fetchTariffs(
        addressId: Int? = uiState.value.selectedLocation?.addressId,
        from: MapPoint? = uiState.value.selectedLocation?.point,
        to: List<MapPoint> = uiState.value.destinations.mapNotNull { it.point }
    ) = viewModelScope.launch {
        if (addressId == null || from == null) return@launch
        _actionState.emit(MapActionState.LoadingTariffs)
        getTariffsUseCase(
            optionIds = listOf(),
            coords = listOf(from.toPair()) + to.map { it.toPair() },
            addressId = addressId
        ).onSuccess { result ->
            _actionState.emit(MapActionState.TariffsLoaded)
            setRoute(result.map.routing.map { MapPoint(it.lat, it.lng) })
            adjustUIForRoute(result.map.routing.isNotEmpty())
            setTariffs(result)
        }.onFailure { setNotFoundState() }
    }

    private fun setTariffs(data: GetTariffsModel) {
        _uiState.update { state ->
            val currentSelected = state.selectedTariff
            val newSelectedTariff = when {
                data.tariff.isEmpty() -> null
                currentSelected == null || currentSelected.id !in data.tariff.map { it.id } -> data.tariff.first()
                else -> currentSelected
            }
            state.copy(
                tariffs = data,
                selectedTariff = newSelectedTariff,
                options = newSelectedTariff?.services ?: emptyList(),
                selectedOptions = if (currentSelected == null || currentSelected.id !in data.tariff.map { it.id }) emptyList() else state.selectedOptions
            )
        }
    }

    fun orderTaxi() = viewModelScope.launch {
        val state = uiState.value
        val selectedLocation = state.selectedLocation
        val selectedTariff = state.selectedTariff

        if (selectedLocation?.addressId == null || selectedTariff?.id == null) return@launch

        val orderDto = OrderTaxiDto(
            dontCallMe = false,
            service = "road",
            addressId = selectedLocation.addressId,
            toPhone = AppPreferences.number,
            comment = uiState.value.comment,
            cardId = (state.selectedPaymentType as? PaymentType.CARD)?.cardId,
            tariffId = selectedTariff.id.toInt(),
            tariffOptions = state.selectedOptions.map { it.id.toInt() },
            paymentType = state.selectedPaymentType.typeName.lowercase(),
            fixedPrice = selectedTariff.fixedType,
            addresses = listOf(
                OrderTaxiDto.Address(
                    addressId = selectedLocation.addressId,
                    lat = selectedLocation.point?.lat.or0(),
                    lng = selectedLocation.point?.lng.or0(),
                    name = selectedLocation.name.orEmpty()
                )
            ) + state.destinations.map {
                OrderTaxiDto.Address(
                    addressId = null,
                    lat = it.point?.lat.or0(),
                    lng = it.point?.lng.or0(),
                    name = it.name.orEmpty()
                )
            }
        )

        orderTaxiUseCase(orderDto).onSuccess { result ->
            getActiveOrders()
            getSetting()
            _uiState.update {
                it.copy(
                    discardOrderButtonState = DiscardOrderButtonState.OpenDrawer,
                    showingOrderId = result.orderId
                )
            }
        }
    }

    fun isPointInsidePolygon(point: MapPoint): Pair<Boolean, Int> {
        addresses.forEach { polygonItem ->
            val vertices = polygonItem.polygons.map { Pair(it.lat, it.lng) }
            var isInside = false
            var addressId = 0
            for (i in vertices.indices) {
                val j = if (i == 0) vertices.size - 1 else i - 1
                val (lat1, lng1) = vertices[i]
                val (lat2, lng2) = vertices[j]
                val intersects = (lng1 > point.lng) != (lng2 > point.lng) &&
                        (point.lat < (lat2 - lat1) * (point.lng - lng1) / (lng2 - lng1) + lat1)
                if (intersects) {
                    isInside = !isInside
                    addressId = polygonItem.addressId
                }
            }
            if (isInside) return Pair(true, addressId)
        }
        return Pair(false, 0)
    }

    private fun MapPoint.toPair() = Pair(lat, lng)

    fun setNotFoundState() {
        _uiState.update {
            it.copy(
                selectedLocation = null,
                tariffs = null,
                timeout = null,
                route = emptyList()
            )
        }
    }

    fun setSelectedTariff(tariff: GetTariffsModel.Tariff) = viewModelScope.launch {
        _uiState.update {
            it.copy(
                selectedTariff = tariff,
                options = tariff.services,
                selectedOptions = emptyList()
            )
        }
//        fetchTariffs()
    }

    fun setSelectedOptions(options: List<GetTariffsModel.Tariff.Service>) {
        _uiState.update { it.copy(selectedOptions = options) }
    }

    fun setSelectedLocation(
        name: String? = uiState.value.selectedLocation?.name,
        point: MapPoint? = uiState.value.selectedLocation?.point,
        addressId: Int? = uiState.value.selectedLocation?.addressId
    ) {
        _uiState.update {
            it.copy(
                selectedLocation = if (addressId == null && point == null && name == null) null else
                    MapUIState.SelectedLocation(
                        name = name,
                        point = point,
                        addressId = addressId
                    )
            )
        }
    }

    fun setDestinations(newDestinations: List<MapUIState.Destination>) {
        _uiState.update { it.copy(destinations = newDestinations) }
        if (newDestinations.isEmpty()) {
            setRoute(emptyList())
        }
        fetchTariffs()
    }

    fun setFoundAddresses(addresses: List<SearchForAddressItemModel>) {
        _uiState.update { it.copy(foundAddresses = addresses) }
    }

    fun setTimeout(timeout: Int?) {
        _uiState.update { it.copy(timeout = timeout) }
    }

    fun setRoute(route: List<MapPoint>) {
        _uiState.update { it.copy(route = route) }
    }

    fun adjustUIForRoute(hasRoute: Boolean) {
        _uiState.update {
            it.copy(
                moveCameraButtonState = if (hasRoute) MoveCameraButtonState.MyRouteView else MoveCameraButtonState.MyLocationView,
                discardOrderButtonState = if (hasRoute) DiscardOrderButtonState.DiscardOrder else DiscardOrderButtonState.OpenDrawer
            )
        }
    }

    fun updateCameraButton(state: MoveCameraButtonState) {
        _uiState.update { it.copy(moveCameraButtonState = state) }
    }

    fun setDrivers(drivers: List<ExecutorModel>) {
        _uiState.update { it.copy(drivers = drivers) }
    }

    fun addOrder(order: ShowOrderModel) {
        _uiState.update { state ->
            state.copy(orders = state.orders + order)
        }
    }

    fun setSelectedOrder(order: ShowOrderModel) {
        _uiState.update { it.copy(selectedOrder = order, showingOrderId = order.id.toInt()) }
        getRouting(order.taxi.routes.map { MapPoint(it.coords.lat, it.coords.lng) })
        order.taxi.routes.firstOrNull()?.let { first ->
            setSelectedLocation(
                point = MapPoint(first.coords.lat, first.coords.lng),
                name = first.fullAddress
            )
        }

        setDestinations(order.taxi.routes.drop(1).map { address ->
            MapUIState.Destination(
                name = address.fullAddress,
                point = address.coords.let { coordinate ->
                    MapPoint(coordinate.lat, coordinate.lng)
                }
            )
        })
    }

    fun setPaymentTypes(paymentTypes: List<CardListItemModel>) {
        _uiState.update { it.copy(paymentTypes = paymentTypes) }
    }

    fun setPaymentType(paymentType: PaymentType) {
        _uiState.update { it.copy(selectedPaymentType = paymentType) }
    }

    fun setSetting(setting: SettingModel?) {
        _uiState.update { it.copy(setting = setting) }
    }

    fun setSelectedDriver(driver: ShowOrderModel?) {
        _uiState.update { it.copy(selectedDriver = driver) }
    }

    fun setComment(comment: String) {
        _uiState.update { it.copy(comment = comment) }
    }

    fun timer(range: IntRange) = flow {
        for (second in range) {
            delay(1.seconds)
            emit(second)
            if (!currentCoroutineContext().isActive) return@flow
        }
    }

    fun infiniteTimer(isActive: Boolean) = flow {
        var seconds = 0
        while (isActive && currentCoroutineContext().isActive) {
            delay(1.seconds)
            emit(seconds)
            seconds++
        }
    }

    fun searchForCars(point: MapPoint) = viewModelScope.launch {
        val tariff = _uiState.value.selectedTariff ?: return@launch
        searchCarUseCase(point.lat, point.lng, tariff.category.id.toInt())
            .onSuccess { result -> setDrivers(result.executors) }
            .onFailure { setDrivers(emptyList()) }
    }

    private fun getSetting() = viewModelScope.launch {
        getSettingUseCase().onSuccess { result ->
            setSetting(result)
            getShow()
        }
    }

    fun cancelRide(orderId: Int) = viewModelScope.launch {
        cancelRideUseCase(orderId)
            .onSuccess {
                AppPreferences.lastOrderId = orderId
                _uiState.update { state ->
                    val newOrders = state
                        .orders
                        .toMutableList()
                        .apply { removeIf { it.id == orderId.toLong() } }
                    state.copy(
                        orders = newOrders,
                        selectedOrder = newOrders.firstOrNull()
                    )
                }
                getSetting()
            }
            .onFailure {}
    }

    fun getCardList() = viewModelScope.launch {
        getCardListUseCase().onSuccess { result -> setPaymentTypes(result) }
    }

    fun getShow() = viewModelScope.launch {
        val selectedOrder = uiState.value.showingOrderId ?: return@launch
        getShowOrderUseCase(selectedOrder).onSuccess { result ->
            setSelectedDriver(result)
            if (result.status != OrderStatus.New) setDrivers(emptyList())
        }
    }

    fun completeOrder() {
        _uiState.update {
            it.copy(
                selectedOrder = null,
                selectedDriver = null,
                showingOrderId = null,
                orders = emptyList(),
                route = emptyList(),
                destinations = emptyList()
            )
        }
    }

    fun rateTheRide(ball: Int) = viewModelScope.launch {
        rateTheRideUseCase(
            ball = ball,
            orderId = uiState.value.showingOrderId.or0(),
            comment = ""
        )
    }

    fun getMe() = viewModelScope.launch {
        getMeUseCase().onSuccess(::setUser)
    }

    fun setUser(user: GetMeModel) {
        _uiState.update { it.copy(user = user) }
    }

    fun getActiveOrders() = viewModelScope.launch {
        getActiveOrdersUseCase()
            .onSuccess { result -> setActiveOrders(result.list) }
    }

    fun setActiveOrders(orders: List<ShowOrderModel>) {
        _uiState.update { it.copy(orders = orders) }
    }

    fun setMoveCameraButtonState(state: MoveCameraButtonState) {
        _uiState.update { it.copy(moveCameraButtonState = state) }
    }

    fun getRouting(list: List<MapPoint>) = viewModelScope.launch {
        if (list.size < 2) return@launch
        val addresses = mutableListOf<GetRoutingDtoItem>()

        addresses.add(
            GetRoutingDtoItem(
                type = "start",
                lat = list.first().lat,
                lng = list.first().lng
            )
        )

        for (address in 1..<addresses.lastIndex) {
            GetRoutingDtoItem(
                type = "point",
                lat = list[address].lat,
                lng = list[address].lng
            )
        }

        addresses.add(
            GetRoutingDtoItem(
                type = "end",
                lat = list.last().lat,
                lng = list.last().lng
            )
        )

        getRoutingUseCase(addresses).onSuccess { result ->
            setRoute(result.routing.map {
                MapPoint(it.lat, it.lng)
            })
            setMoveCameraButtonState(MoveCameraButtonState.MyRouteView)
        }.onFailure {
            Log.d("freaking", "getRouting: ")
        }
    }
}