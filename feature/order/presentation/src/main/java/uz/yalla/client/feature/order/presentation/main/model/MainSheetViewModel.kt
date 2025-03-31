package uz.yalla.client.feature.order.presentation.main.model

import android.util.Log
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import uz.yalla.client.core.common.sheet.search_address.SearchByNameSheetValue
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapViewValue
import uz.yalla.client.core.data.enums.PaymentType
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.core.data.mapper.orFalse
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.SelectedLocation
import uz.yalla.client.core.domain.model.ServiceModel
import uz.yalla.client.feature.map.domain.model.response.PolygonRemoteItem
import uz.yalla.client.feature.map.domain.usecase.GetPolygonUseCase
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.feature.order.domain.usecase.order.OrderTaxiUseCase
import uz.yalla.client.feature.order.domain.usecase.tariff.GetTariffsUseCase
import uz.yalla.client.feature.order.domain.usecase.tariff.GetTimeOutUseCase
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.main.MAIN_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.main.view.MainSheet
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.FooterIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.OrderCommentSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.OrderTaxiSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.PaymentMethodSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.TariffInfoSheetIntent
import uz.yalla.client.feature.payment.domain.usecase.GetCardListUseCase
import kotlin.time.Duration.Companion.seconds

class MainSheetViewModel(
    private val getPolygonUseCase: GetPolygonUseCase,
    private val getTariffsUseCase: GetTariffsUseCase,
    private val orderTaxiUseCase: OrderTaxiUseCase,
    private val getCardListUseCase: GetCardListUseCase,
    private val getTimeOutUseCase: GetTimeOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainSheetState())
    val uiState: StateFlow<MainSheetState> = _uiState.asStateFlow()

    private val _sheetVisibilityListener: Channel<Unit> = Channel(Channel.BUFFERED)
    val sheetVisibilityListener = _sheetVisibilityListener.receiveAsFlow()

    private val _isPolygonLoading = MutableStateFlow(false)

    val buttonAndOptionsState = uiState
        .map { state ->
            val isTariffValidWithOptions = state.selectedOptions.all { selected ->
                state.options.any { option ->
                    selected.cost == option.cost && selected.name == option.name
                }
            }

            val isButtonEnabled = state.selectedTariff != null &&
                    isTariffValidWithOptions &&
                    !(state.selectedTariff.isSecondAddressMandatory && state.destinations.isEmpty())

            ButtonAndOptionsState(
                isButtonEnabled = isButtonEnabled,
                isTariffValidWithOptions = isTariffValidWithOptions
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ButtonAndOptionsState(
                isButtonEnabled = false,
                isTariffValidWithOptions = true
            )
        )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            uiState
                .distinctUntilChangedBy { Pair(it.selectedTariff, it.selectedLocation) }
                .collectLatest { state ->
                    state.selectedLocation?.point?.let {
                        if (state.selectedTariff != null) {
                            getTimeout(it)
                        }
                    }
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(10.seconds)
                uiState.value.selectedLocation?.point?.let {
                    if (uiState.value.selectedTariff != null) {
                        getTimeout(it)
                    }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            uiState
                .distinctUntilChangedBy { Pair(it.drivers, it.timeout) }
                .collectLatest { state ->
                    MainSheet.mutableIntentFlow.emit(
                        OrderTaxiSheetIntent.SetTimeout(
                            timeout = state.timeout,
                            drivers = state.drivers
                        )
                    )
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            getPolygon()
            AppPreferences.entryLocation.let { (lat, lng) ->
                if (lat != 0.0 && lng != 0.0) {
                    val initialLocation = SelectedLocation(
                        name = null,
                        point = MapPoint(lat, lng),
                        addressId = 0
                    )
                    setSelectedLocation(initialLocation)
                }
            }
        }
    }

    fun onIntent(intent: MainSheetIntent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (intent) {
                is OrderTaxiSheetIntent.SelectTariff -> {
                    if (intent.wasSelected) {
                        _sheetVisibilityListener.send(Unit)
                    } else {
                        setSelectedTariff(intent.tariff)
                        // Re-fetch tariffs when a tariff is selected to ensure proper sync
                        uiState.value.selectedLocation?.point?.let { point ->
                            uiState.value.selectedLocation?.addressId?.takeIf { it != 0 }
                                ?.let { addressId ->
                                    getTariffs(addressId)
                                }
                        }
                    }
                }

                is OrderTaxiSheetIntent.CurrentLocationClick -> {
                    setSearchByNameSheetVisibility(SearchByNameSheetValue.FOR_START)
                }

                is OrderTaxiSheetIntent.DestinationClick -> {
                    setSearchByNameSheetVisibility(SearchByNameSheetValue.FOR_DEST)
                }

                is OrderTaxiSheetIntent.AddNewDestinationClick -> {
                    setAddDestinationSheetVisibility(true)
                }

                is OrderCommentSheetIntent.OnDismissRequest -> {
                    _uiState.update {
                        it.copy(
                            isOrderCommentSheetVisible = false,
                            comment = intent.comment
                        )
                    }
                }

                is TariffInfoSheetIntent.ClearOptions -> {
                    setSelectedOptions(emptyList())
                    // Re-fetch tariffs when options change
                    refreshTariffsIfPossible()
                }

                is TariffInfoSheetIntent.OptionsChange -> {
                    setSelectedOptions(intent.options)
                    refreshTariffsIfPossible()
                }

                is TariffInfoSheetIntent.ChangeShadowVisibility -> {
                    _uiState.update { it.copy(shadowVisibility = intent.visible) }
                }

                is TariffInfoSheetIntent.ClickComment -> {
                    _uiState.update { it.copy(isOrderCommentSheetVisible = true) }
                }

                is FooterIntent.ClearOptions -> {
                    setSelectedOptions(emptyList())
                    refreshTariffsIfPossible()
                }

                is FooterIntent.CreateOrder -> {
                    orderTaxi()
                }

                is FooterIntent.ClickPaymentButton -> {
                    _uiState.update { it.copy(isPaymentMethodSheetVisible = true) }
                }

                is FooterIntent.ChangeSheetVisibility -> {
                    _sheetVisibilityListener.send(Unit)
                }

                is PaymentMethodSheetIntent.OnDismissRequest -> {
                    _uiState.update { it.copy(isPaymentMethodSheetVisible = false) }
                }

                is PaymentMethodSheetIntent.OnSelectPaymentType -> {
                    AppPreferences.paymentType = intent.paymentType
                    setPaymentType(intent.paymentType)
                }

                else -> {
                    MainSheet.mutableIntentFlow.emit(intent)
                }
            }
        }
    }

    private fun refreshTariffsIfPossible() {
        uiState.value.selectedLocation?.let { location ->
            location.addressId.takeIf { it != 0 }?.let { addressId ->
                getTariffs(addressId)
            }
        }
    }

    fun getPolygon() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_isPolygonLoading.value) return@launch

            _isPolygonLoading.value = true

            try {
                getPolygonUseCase().onSuccess { polygon ->
                    Log.d("MainSheetViewModel", "Polygon loaded: ${polygon.size} regions")
                    _uiState.update { it.copy(polygon = polygon) }

                    uiState.value.selectedLocation?.let { location ->
                        if (location.addressId == 0 && location.point != null) {
                            processLocation(location)
                        }
                    }
                }
            } finally {
                _isPolygonLoading.value = false
            }
        }
    }

    fun getCardList() {
        viewModelScope.launch(Dispatchers.IO) {
            getCardListUseCase().onSuccess { cardList ->
                _uiState.update { it.copy(cardList = cardList) }
            }
        }
    }

    fun setSelectedLocation(selectedLocation: SelectedLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            val point = selectedLocation.point

            if (point == null) {
                val lastLat = AppPreferences.entryLocation.first
                val lastLng = AppPreferences.entryLocation.second

                if (lastLat != 0.0 && lastLng != 0.0) {
                    val updatedLocation = selectedLocation.copy(
                        point = MapPoint(lastLat, lastLng)
                    )
                    processLocation(updatedLocation)
                } else {
                    _uiState.update { it.copy(selectedLocation = selectedLocation) }
                }
            } else {
                processLocation(selectedLocation)
            }
        }
    }

    private suspend fun processLocation(selectedLocation: SelectedLocation) {
        val point = selectedLocation.point ?: return

        if (uiState.value.polygon.isEmpty()) {
            if (!_isPolygonLoading.value) {
                getPolygon()
                var retries = 0
                while (uiState.value.polygon.isEmpty() && retries < 3) {
                    delay(500)
                    retries++
                }
            }
        }

        val (isInsidePolygon, addressId) = isPointInsidePolygon(point)

        val updatedLocation = selectedLocation.copy(addressId = addressId)

        _uiState.update { it.copy(selectedLocation = updatedLocation) }

        if (isInsidePolygon && addressId != 0) {
            suspendGetTariffs(addressId)

            MainSheet.mutableIntentFlow.emit(
                OrderTaxiSheetIntent.SetServiceState(available = true)
            )
        } else {
            setSelectedTariff(null)
            handleTariffsFailure()

            MainSheet.mutableIntentFlow.emit(
                OrderTaxiSheetIntent.SetServiceState(available = false)
            )
        }
    }

    private suspend fun suspendGetTariffs(addressId: Int): Result<GetTariffsModel> =
        supervisorScope {
            val from = uiState.value.selectedLocation?.point?.toPair()
                ?: return@supervisorScope Result.failure(IllegalStateException("No location selected"))
            val to = uiState.value.destinations.mapNotNull { it.point?.toPair() }
            val selectedOptionsIds = uiState.value.selectedOptions.map { it.id }

            val result = getTariffsUseCase(
                addressId = addressId,
                optionIds = selectedOptionsIds,
                coords = listOf(from, *to.toTypedArray())
            )

            result.fold(
                onSuccess = { tariffsModel ->
                    handleTariffsSuccess(tariffsModel)
                },
                onFailure = {
                    handleTariffsFailure()
                }
            )

            return@supervisorScope result
        }

    fun setDestination(destinations: List<Destination>) {
        _uiState.update { it.copy(destinations = destinations) }
        viewModelScope.launch(Dispatchers.IO) {
            refreshTariffsIfPossible()
        }
    }

    fun setPaymentType(type: PaymentType) {
        _uiState.update { it.copy(selectedPaymentType = type) }
    }

    private fun setSelectedTariff(tariff: GetTariffsModel.Tariff?) {
        _uiState.update {
            it.copy(
                selectedTariff = tariff,
                options = tariff?.services ?: emptyList(),
                isSecondaryAddressMandatory = tariff?.isSecondAddressMandatory.orFalse()
            )
        }
    }

    private fun setSelectedOptions(options: List<ServiceModel>) {
        _uiState.update { it.copy(selectedOptions = options) }
    }

    fun setFooterHeight(height: Dp) {
        _uiState.update { it.copy(footerHeight = height) }
        SheetCoordinator.updateSheetHeight(
            route = MAIN_SHEET_ROUTE,
            height = height + uiState.value.sheetHeight
        )
    }

    fun setSheetHeight(height: Dp) {
        _uiState.update { it.copy(sheetHeight = height) }
        SheetCoordinator.updateSheetHeight(
            route = MAIN_SHEET_ROUTE,
            height = height + uiState.value.footerHeight
        )
    }

    private suspend fun isPointInsidePolygon(point: MapPoint): Pair<Boolean, Int> = coroutineScope {
        val polygon = uiState.value.polygon
        if (polygon.isEmpty()) {
            return@coroutineScope Pair(false, 0)
        }

        val results = polygon.map { polygonItem ->
            async(Dispatchers.Default) {
                isPointInPolygon(point, polygonItem)
            }
        }

        results.awaitAll().lastOrNull { it.first } ?: Pair(false, 0)
    }

    private fun isPointInPolygon(
        point: MapPoint,
        polygonItem: PolygonRemoteItem
    ): Pair<Boolean, Int> {
        val vertices = polygonItem.polygons.map { Pair(it.lat, it.lng) }
        var isInside = false

        for (i in vertices.indices) {
            val j = if (i == 0) vertices.size - 1 else i - 1
            val (lat1, lng1) = vertices[i]
            val (lat2, lng2) = vertices[j]

            val intersects = (lng1 > point.lng) != (lng2 > point.lng) &&
                    (point.lat < (lat2 - lat1) * (point.lng - lng1) / (lng2 - lng1) + lat1)

            if (intersects) {
                isInside = !isInside
            }
        }

        return if (isInside) Pair(true, polygonItem.addressId) else Pair(false, 0)
    }

    private fun getTariffs(addressId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val from = uiState.value.selectedLocation?.point?.toPair() ?: return@launch
            val to = uiState.value.destinations.mapNotNull { it.point?.toPair() }
            val selectedOptionsIds = uiState.value.selectedOptions.map { it.id }

            getTariffsUseCase(
                addressId = addressId,
                optionIds = selectedOptionsIds,
                coords = listOf(from, *to.toTypedArray())
            ).fold(
                onSuccess = { tariffsModel ->
                    handleTariffsSuccess(tariffsModel)
                },
                onFailure = {
                    handleTariffsFailure()
                }
            )
        }
    }

    private fun getTimeout(point: MapPoint) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState.value.selectedTariff?.id?.let { tariffId ->
                getTimeOutUseCase(point.lat, point.lng, tariffId).onSuccess { timeout ->
                    _uiState.update {
                        it.copy(
                            timeout = timeout.timeout,
                            drivers = timeout.executors
                        )
                    }
                }
            }
        }
    }

    private fun handleTariffsSuccess(tariffsModel: GetTariffsModel) {
        _uiState.update { currentState ->
            val currentSelectedTariff = currentState.selectedTariff
            val newSelectedTariff =
                determineSelectedTariff(currentSelectedTariff, tariffsModel.tariff)

            currentState.copy(
                tariffs = tariffsModel,
                selectedTariff = newSelectedTariff,
                options = newSelectedTariff?.services ?: emptyList(),
                selectedOptions = if (currentSelectedTariff?.id != newSelectedTariff?.id)
                    emptyList()
                else
                    currentState.selectedOptions
            )
        }
    }

    private fun determineSelectedTariff(
        currentTariff: GetTariffsModel.Tariff?,
        availableTariffs: List<GetTariffsModel.Tariff>
    ): GetTariffsModel.Tariff? {
        return when {
            availableTariffs.isEmpty() -> null
            currentTariff == null -> availableTariffs.firstOrNull()
            availableTariffs.none { it.id == currentTariff.id } -> availableTariffs.firstOrNull()
            else -> availableTariffs.find { it.id == currentTariff.id }
                ?: availableTariffs.firstOrNull()
        }
    }

    private fun handleTariffsFailure() {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            _uiState.update {
                it.copy(
                    tariffs = null,
                    selectedTariff = null,
                    options = emptyList(),
                    selectedOptions = emptyList()
                )
            }
        }
    }

    private fun orderTaxi() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState.value.mapToOrderTaxiDto()?.let { model ->
                orderTaxiUseCase(model).onSuccess { order ->
                    _uiState.update { it.copy(orderId = order.orderId) }
                    MainSheet.mutableIntentFlow.emit(
                        OrderTaxiSheetIntent.OrderCreated(order.orderId)
                    )
                }
            }
        }
    }

    private fun MapPoint.toPair() = Pair(lat, lng)

    fun setSearchByNameSheetVisibility(value: SearchByNameSheetValue) {
        _uiState.update { it.copy(searchByNameSheetVisible = value) }
    }

    fun setSelectFromMapViewVisibility(value: SelectFromMapViewValue) {
        _uiState.update { it.copy(selectFromMapViewVisible = value) }
    }

    fun setAddDestinationSheetVisibility(visible: Boolean) {
        _uiState.update { it.copy(addDestinationSheetVisible = visible) }
    }
}