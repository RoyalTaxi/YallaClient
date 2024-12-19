package uz.ildam.technologies.yalla.android.ui.screens.map

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
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.core.domain.model.ExecutorModel
import uz.ildam.technologies.yalla.feature.map.domain.model.map.PolygonRemoteItem
import uz.ildam.technologies.yalla.feature.map.domain.model.map.SearchForAddressItemModel
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.GetAddressNameUseCase
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.GetPolygonUseCase
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.SearchForAddressUseCase
import uz.ildam.technologies.yalla.feature.order.domain.model.request.OrderTaxiDto
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.SettingModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.ShowOrderModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.ildam.technologies.yalla.feature.order.domain.usecase.order.CancelRideUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.order.GetSettingUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.order.OrderTaxiUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.order.SearchCarUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff.GetTariffsUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff.GetTimeOutUseCase
import uz.ildam.technologies.yalla.feature.payment.domain.model.CardListItemModel
import uz.ildam.technologies.yalla.feature.payment.domain.usecase.GetCardListUseCase
import kotlin.time.Duration.Companion.seconds

class MapViewModel(
    private val getPolygonUseCase: GetPolygonUseCase,
    private val getAddressNameUseCase: GetAddressNameUseCase,
    private val getTariffsUseCase: GetTariffsUseCase,
    private val getTimeOutUseCase: GetTimeOutUseCase,
    private val searchForAddressUseCase: SearchForAddressUseCase,
    private val orderTaxiUseCase: OrderTaxiUseCase,
    private val searchCarUseCase: SearchCarUseCase,
    private val getSettingUseCase: GetSettingUseCase,
    private val cancelRideUseCase: CancelRideUseCase,
    private val getCardListUseCase: GetCardListUseCase,
    private val getShowOrderUseCase: GetShowOrderUseCase
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
        when (val result = getPolygonUseCase()) {
            is Result.Success -> {
                addresses = result.data
                _actionState.emit(MapActionState.PolygonLoaded)
            }

            is Result.Error -> changeStateToNotFound()
        }
    }

    fun getAddressDetails(point: MapPoint) = viewModelScope.launch {
        if (point.lat == 0.0 || point.lng == 0.0) return@launch
        var addressId = 0
        _actionState.emit(MapActionState.LoadingAddressId)
        if (addresses.isEmpty()) fetchPolygons()
        else addresses
            .firstOrNull {
                val (isInsidePolygonTemp, addressIdTemp) = isPointInsidePolygon(point = point)
                if (isInsidePolygonTemp) addressId = addressIdTemp
                isInsidePolygonTemp
            }?.let {
                updateSelectedLocation(addressId = addressId, point = point)
                _actionState.emit(MapActionState.AddressIdLoaded(addressId.toLong()))
            } ?: run {
            updateSelectedLocation(addressId = null)
        }
        fetchAddressName(point)
    }

    private fun fetchAddressName(point: MapPoint) = viewModelScope.launch {
        _actionState.emit(MapActionState.LoadingAddressName)
        when (val result = getAddressNameUseCase(point.lat, point.lng)) {
            is Result.Success -> _actionState.emit(MapActionState.AddressNameLoaded(result.data.displayName))
            is Result.Error -> {
                changeStateToNotFound()
            }
        }
    }

    fun getTimeout(point: MapPoint) = viewModelScope.launch {
        val selectedTariff = _uiState.value.selectedTariff
            ?: _uiState.value.tariffs?.tariff?.firstOrNull()

        selectedTariff?.let { tariff ->
            _actionState.emit(MapActionState.LoadingTariffs)
            when (val result = getTimeOutUseCase(
                lat = point.lat,
                lng = point.lng,
                tariffId = tariff.category.id.toInt()
            )) {
                is Result.Error -> changeStateToNotFound()
                is Result.Success -> _uiState.update {
                    it.copy(
                        timeout = result.data.timeout,
                        drivers = result.data.executors
                    )
                }
            }
        }
    }

    fun fetchTariffs(
        addressId: Int? = uiState.value.selectedLocation?.addressId,
        from: MapPoint? = uiState.value.selectedLocation?.point,
        to: List<MapPoint> = uiState.value.destinations.mapNotNull { it.point }
    ) = viewModelScope.launch {
        _actionState.emit(MapActionState.LoadingTariffs)
        if (addressId != null && from != null) {
            when (
                val result = getTariffsUseCase(
                    optionIds = listOf(),
                    coords = listOf(from.toPair()) + to.map { it.toPair() },
                    addressId = addressId
                )
            ) {
                is Result.Success -> {
                    _actionState.emit(MapActionState.TariffsLoaded)
                    updateUIState(route = result.data.map.routing.map { MapPoint(it.lat, it.lng) })
                    if (result.data.map.routing.isNotEmpty()) updateUIState(
                        moveCameraButtonState = MoveCameraButtonState.MyRouteView,
                        discardOrderButtonState = DiscardOrderButtonState.DiscardOrder
                    )
                    else updateUIState(
                        moveCameraButtonState = MoveCameraButtonState.MyLocationView,
                        discardOrderButtonState = DiscardOrderButtonState.OpenDrawer
                    )
                    updateTariffs(result.data)
                }

                is Result.Error -> changeStateToNotFound()
            }
        }
    }

    private fun updateTariffs(data: GetTariffsModel) {
        _uiState.update { it.copy(tariffs = data) }
        val selectedTariff = _uiState.value.selectedTariff
        if (data.tariff.isNotEmpty())
            if (selectedTariff == null) updateUIState(
                selectedTariff = data.tariff.first(),
                options = data.tariff.first().services
            )
            else if (selectedTariff.id !in data.tariff.map { it.id }) updateUIState(
                selectedTariff = data.tariff.firstOrNull(),
                options = data.tariff[0].services
            )

    }

    fun searchForAddress(query: String, point: MapPoint) = viewModelScope.launch {
        when (val result = searchForAddressUseCase(
            query = query,
            lat = point.lat,
            lng = point.lng
        )) {
            is Result.Error -> _uiState.update { it.copy(foundAddresses = emptyList()) }
            is Result.Success -> _uiState.update { it.copy(foundAddresses = result.data) }
        }
    }

    fun orderTaxi() = viewModelScope.launch {
        if (
            uiState.value.selectedLocation?.addressId != null &&
            uiState.value.selectedTariff?.id != null
        ) when (val result = orderTaxiUseCase(
            OrderTaxiDto(
                dontCallMe = false,
                service = "road",
                addressId = uiState.value.selectedLocation!!.addressId!!,
                toPhone = AppPreferences.number,
                comment = "",
                cardId = if (uiState.value.selectedPaymentType is PaymentType.CARD) {
                    (uiState.value.selectedPaymentType as PaymentType.CARD).cardId
                } else null,
                tariffId = uiState.value.selectedTariff!!.id.toInt(),
                tariffOptions = uiState.value.selectedOptions.map { it.id.toInt() },
                paymentType = uiState.value.selectedPaymentType.typeName.lowercase(),
                fixedPrice = uiState.value.selectedTariff!!.fixedType,
                addresses = uiState.value.destinations.map { destination ->
                    OrderTaxiDto.Address(
                        addressId = null,
                        lat = destination.point?.lat.or0(),
                        lng = destination.point?.lng.or0(),
                        name = destination.name.orEmpty()
                    )
                } + uiState.value.selectedLocation!!.let {
                    OrderTaxiDto.Address(
                        addressId = it.addressId,
                        lat = it.point?.lat.or0(),
                        lng = it.point?.lng.or0(),
                        name = it.name.orEmpty()
                    )
                }
            )
        )) {
            is Result.Error -> {}

            is Result.Success -> {
                val orders = uiState.value.orders.toMutableList()
                orders.add(result.data.orderId)
                _uiState.update {
                    it.copy(
                        orders = orders,
                        selectedOrder = result.data.orderId
                    )
                }
                getSetting()
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

    fun changeStateToNotFound() {
        updateUIState(
            selectedLocation = null,
            tariffs = null,
            timeout = null
        )
    }

    fun updateUIState(
        // Location-related properties
        markerSelectedLocation: MapUIState.SelectedLocation? = _uiState.value.markerSelectedLocation,
        selectedLocation: MapUIState.SelectedLocation? = _uiState.value.selectedLocation,
        destinations: List<MapUIState.Destination> = _uiState.value.destinations,
        foundAddresses: List<SearchForAddressItemModel> = _uiState.value.foundAddresses,
        route: List<MapPoint> = _uiState.value.route,

        // Tariff-related properties
        tariffs: GetTariffsModel? = _uiState.value.tariffs,
        selectedTariff: GetTariffsModel.Tariff? = _uiState.value.selectedTariff,
        options: List<GetTariffsModel.Tariff.Service> = _uiState.value.options,
        selectedOptions: List<GetTariffsModel.Tariff.Service> = _uiState.value.selectedOptions,

        // Order-related properties
        timeout: Int? = _uiState.value.timeout,
//        isSearchingForCars: Boolean = _uiState.value.isSearchingForCars,
        orders: List<Int> = _uiState.value.orders,
        selectedOrder: Int? = _uiState.value.selectedOrder,

        // Payment-related properties
        selectedPaymentType: PaymentType = _uiState.value.selectedPaymentType,
        paymentTypes: List<CardListItemModel> = _uiState.value.paymentTypes,

        // Driver-related properties
        drivers: List<ExecutorModel> = _uiState.value.drivers,
        selectedDriver: ShowOrderModel? = _uiState.value.selectedDriver,

        // Setting-related properties
        setting: SettingModel? = _uiState.value.setting,

        // UI State-related properties
        moveCameraButtonState: MoveCameraButtonState = _uiState.value.moveCameraButtonState,
        discardOrderButtonState: DiscardOrderButtonState = _uiState.value.discardOrderButtonState,
    ) {
        _uiState.update {
            it.copy(
                // Location-related properties
                markerSelectedLocation = markerSelectedLocation,
                selectedLocation = selectedLocation,
                destinations = destinations,
                foundAddresses = foundAddresses,
                route = route,

                // Tariff-related properties
                tariffs = tariffs,
                selectedTariff = selectedTariff,
                options = options,
                selectedOptions = selectedOptions,

                // Order-related properties
                timeout = timeout,
//                isSearchingForCars = isSearchingForCars,
                orders = orders,
                selectedOrder = selectedOrder,

                // Payment-related properties
                selectedPaymentType = selectedPaymentType,
                paymentTypes = paymentTypes,

                // Driver-related properties
                drivers = drivers,
                selectedDriver = selectedDriver,

                // Setting-related properties
                setting = setting,

                // UI State-related properties
                moveCameraButtonState = moveCameraButtonState,
                discardOrderButtonState = discardOrderButtonState
            )
        }
    }

    fun updateSelectedTariff(tariff: GetTariffsModel.Tariff) {
        _uiState.update {
            it.copy(
                selectedTariff = tariff,
                options = tariff.services,
                selectedOptions = emptyList()
            )
        }

        fetchTariffs()
    }

    fun updateSelectedOptions(options: List<GetTariffsModel.Tariff.Service>) {
        _uiState.update { it.copy(selectedOptions = options) }
    }

    fun updateSelectedLocation(
        name: String? = _uiState.value.selectedLocation?.name,
        point: MapPoint? = _uiState.value.selectedLocation?.point,
        addressId: Int? = _uiState.value.selectedLocation?.addressId
    ) {
        _uiState.update {
            it.copy(
                selectedLocation = MapUIState.SelectedLocation(
                    name = name,
                    point = point,
                    addressId = addressId
                )
            )
        }
    }

    fun updateDestinations(newDestinations: List<MapUIState.Destination>) {
        _uiState.update { it.copy(destinations = newDestinations) }
        if (newDestinations.isEmpty()) updateUIState(route = emptyList())
        fetchTariffs()
    }

    fun timer(range: IntRange) = flow {
        range.forEach { seconds ->
            delay(1.seconds)
            emit(seconds)
            if (!currentCoroutineContext().isActive) return@flow
        }
    }

    fun searchForCars(point: MapPoint) = viewModelScope.launch {
        val tariff = _uiState.value.selectedTariff ?: return@launch
        when (val result = searchCarUseCase(point.lat, point.lng, tariff.category.id.toInt())) {
            is Result.Error -> updateUIState(drivers = emptyList())
            is Result.Success -> updateUIState(drivers = result.data.executors)
        }
    }

    private fun getSetting() = viewModelScope.launch {
        when (val result = getSettingUseCase()) {
            is Result.Error -> {}
            is Result.Success -> {
                updateUIState(setting = result.data)
                getShow()
            }
        }
    }

    fun cancelRide(orderId: Int) = viewModelScope.launch {
        when (cancelRideUseCase(orderId)) {
            is Result.Error -> {}
            is Result.Success -> {
                AppPreferences.lastOrderId = orderId
                val orders = uiState.value.orders.toMutableList()
                orders.remove(orderId)
                updateUIState(
                    orders = orders,
                    selectedOrder = orders.firstOrNull()
                )
            }
        }
    }

    fun getCardList() = viewModelScope.launch {
        when (val result = getCardListUseCase()) {
            is Result.Error -> {}
            is Result.Success -> updateUIState(paymentTypes = result.data)
        }
    }

    fun getShow() = viewModelScope.launch {
        uiState.value.selectedOrder?.let {
            when (val result = getShowOrderUseCase(it)) {
                is Result.Error -> {}
                is Result.Success -> {
                    updateUIState(
                        selectedDriver = result.data,
                        drivers = emptyList()
                    )
                }
            }
        }
    }
}