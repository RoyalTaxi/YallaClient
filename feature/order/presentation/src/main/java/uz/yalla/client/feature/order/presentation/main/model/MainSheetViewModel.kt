package uz.yalla.client.feature.order.presentation.main.model

import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import uz.yalla.client.core.common.sheet.search_address.SearchByNameSheetValue
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapViewValue
import uz.yalla.client.core.data.mapper.orFalse
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.core.domain.model.SelectedLocation
import uz.yalla.client.core.domain.model.ServiceModel
import uz.yalla.client.feature.map.domain.model.response.PolygonRemoteItem
import uz.yalla.client.feature.map.domain.usecase.GetPolygonUseCase
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.order.domain.usecase.order.OrderTaxiUseCase
import uz.yalla.client.feature.order.domain.usecase.tariff.GetTariffsUseCase
import uz.yalla.client.feature.order.domain.usecase.tariff.GetTimeOutUseCase
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.main.MAIN_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.main.view.MainSheetAction
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.FooterIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.OrderCommentSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.OrderTaxiSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.PaymentMethodSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.TariffInfoSheetIntent
import uz.yalla.client.feature.payment.domain.usecase.GetCardListUseCase

class MainSheetViewModel(
    private val getPolygonUseCase: GetPolygonUseCase,
    private val getTariffsUseCase: GetTariffsUseCase,
    private val orderTaxiUseCase: OrderTaxiUseCase,
    private val getCardListUseCase: GetCardListUseCase,
    private val getTimeOutUseCase: GetTimeOutUseCase,
    private val getShowOrderUseCase: GetShowOrderUseCase,
    private val prefs: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainSheetState())
    val uiState = _uiState.asStateFlow()

    private val _sheetVisibilityListener = Channel<Unit>(Channel.BUFFERED)
    val sheetVisibilityListener = _sheetVisibilityListener.receiveAsFlow()

    private val _isPolygonLoading = MutableStateFlow(false)

    val timeoutUpdateTrigger = uiState
        .map { it.selectedTariff?.id to it.selectedLocation?.point }
        .distinctUntilChanged()

    val buttonAndOptionsState = uiState
        .map(::mapToButtonAndOptionsState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ButtonAndOptionsState()
        )

    fun updateTimeout() {
        uiState.value.run {
            selectedLocation?.point?.takeIf { selectedTariff != null }?.let {
                getTimeout(it)
            }
        }
    }

    fun observePaymentMethod() = viewModelScope.launch {
        prefs.paymentType.collectLatest { type ->
            _uiState.update { it.copy(selectedPaymentType = type) }
        }
    }

    fun observeChannels() {
        viewModelScope.launch {
            MainSheetChannel.actionChannel.collectLatest { action ->
                when (action) {
                    is MainSheetAction.SetDestination -> setDestination(action.destinations)
                    is MainSheetAction.SetLocation -> setSelectedLocation(action.location)
                    is MainSheetAction.SetBonusInfoVisibility -> setBonusInfoVisibility(action.visible)
                }
            }
        }
    }

    fun onIntent(intent: MainSheetIntent) {
        viewModelScope.launch {
            when (intent) {
                is OrderTaxiSheetIntent.SelectTariff -> handleSelectTariff(intent)
                is OrderTaxiSheetIntent.CurrentLocationClick -> setSearchByNameSheetVisibility(
                    SearchByNameSheetValue.FOR_START
                )

                is OrderTaxiSheetIntent.DestinationClick -> handleDestinationClick()
                is OrderTaxiSheetIntent.AddNewDestinationClick -> setAddDestinationSheetVisibility(
                    true
                )

                is OrderCommentSheetIntent.OnDismissRequest -> _uiState.update {
                    it.copy(
                        isOrderCommentSheetVisible = false,
                        comment = intent.comment
                    )
                }

                is TariffInfoSheetIntent.ClearOptions, is FooterIntent.ClearOptions -> clearAndRefreshTariffs()
                is TariffInfoSheetIntent.OptionsChange -> updateOptionsAndRefreshTariffs(intent.options)
                is TariffInfoSheetIntent.ChangeShadowVisibility -> _uiState.update {
                    it.copy(
                        isShadowVisible = intent.visible
                    )
                }

                is TariffInfoSheetIntent.ClickComment -> _uiState.update {
                    it.copy(
                        isOrderCommentSheetVisible = true
                    )
                }

                is FooterIntent.CreateOrder -> orderTaxi()
                is FooterIntent.ClickPaymentButton -> _uiState.update {
                    it.copy(
                        isPaymentMethodSheetVisible = true
                    )
                }

                is FooterIntent.ChangeSheetVisibility -> _sheetVisibilityListener.send(Unit)
                is PaymentMethodSheetIntent.OnDismissRequest -> _uiState.update {
                    it.copy(
                        isPaymentMethodSheetVisible = false
                    )
                }

                is PaymentMethodSheetIntent.OnSelectPaymentType -> updatePaymentType(intent.paymentType)
                is PaymentMethodSheetIntent.EnableBonus -> {
                    setBonusAmountSheetVisibility(true)
                }

                is PaymentMethodSheetIntent.DisableBonus -> {
                    setBonusAmount(0)
                }

                else -> MainSheetChannel.intentChannel.emit(intent)
            }
        }
    }

    private fun mapToButtonAndOptionsState(state: MainSheetState): ButtonAndOptionsState {
        val isTariffValid =
            state.selectedOptions.all { opt -> state.options.any { it.name == opt.name && it.cost == opt.cost } }
        val isButtonEnabled =
            state.selectedTariff != null && isTariffValid && (!state.selectedTariff.isSecondAddressMandatory || state.destinations.isNotEmpty())
        return ButtonAndOptionsState(isButtonEnabled, isTariffValid)
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

    private fun refreshTariffsIfPossible() {
        uiState.value.selectedLocationId?.let { getTariffs(it) }
    }

    fun observeTimeoutAndDrivers() {
        viewModelScope.launch {
            uiState.distinctUntilChangedBy { it.drivers to it.timeout }
                .collectLatest {
                    MainSheetChannel.intentChannel.emit(
                        OrderTaxiSheetIntent.SetTimeout(
                            it.timeout,
                            it.drivers
                        )
                    )
                }
        }
    }

    fun observeOrderIdForShowOrder() {
        viewModelScope.launch {
            uiState.distinctUntilChangedBy { it.orderId }
                .collectLatest { it.orderId?.let(::getShowOrder) }
        }
    }

    private fun handleSelectTariff(intent: OrderTaxiSheetIntent.SelectTariff) {
        if (intent.wasSelected) _sheetVisibilityListener.trySend(Unit).isSuccess
        else {
            setSelectedTariff(intent.tariff)
            uiState.value.selectedLocationId?.let(::getTariffs)
        }
    }

    private fun handleDestinationClick() {
        if (uiState.value.destinations.size > 1)
            setArrangeDestinationsSheetVisibility(true)
        else
            setSearchByNameSheetVisibility(SearchByNameSheetValue.FOR_DEST)
    }

    private fun clearAndRefreshTariffs() = updateOptionsAndRefreshTariffs(emptyList())

    private fun updateOptionsAndRefreshTariffs(options: List<ServiceModel>) {
        setSelectedOptions(options)
        refreshTariffsIfPossible()
    }

    private fun setSelectedLocation(selectedLocation: SelectedLocation) {
        viewModelScope.launch {
            val fallback = prefs.entryLocation.first()
            val point = selectedLocation.point
                ?: fallback.takeIf { it.first != 0.0 && it.second != 0.0 }
                    ?.let { MapPoint(it.first, it.second) }

            val updated = selectedLocation.copy(point = point)
            _uiState.update { it.copy(selectedLocation = updated) }
            point?.let { processLocation(updated) }
        }
    }

    private suspend fun processLocation(location: SelectedLocation) {
        if (uiState.value.polygon.isEmpty()) getPolygon()
        val (inside, addressId) = isPointInsidePolygon(location.point ?: return)
        _uiState.update { it.copy(selectedLocationId = addressId) }

        if (inside && addressId != null) {
            suspendGetTariffs(addressId)
            MainSheetChannel.intentChannel.emit(OrderTaxiSheetIntent.SetServiceState(true))
        } else {
            setSelectedTariff(null)
            handleTariffsFailure()
            MainSheetChannel.intentChannel.emit(OrderTaxiSheetIntent.SetServiceState(false))
        }
    }

    private suspend fun isPointInsidePolygon(point: MapPoint): Pair<Boolean, Int?> =
        coroutineScope {
            val results = uiState.value.polygon.map { async { isPointInPolygon(point, it) } }
            results.awaitAll().firstOrNull { it.first } ?: Pair(false, null)
        }

    private fun isPointInPolygon(point: MapPoint, polygon: PolygonRemoteItem): Pair<Boolean, Int?> {
        val vertices = polygon.polygons.map { it.lat to it.lng }
        var inside = false

        for (i in vertices.indices) {
            val (lat1, lng1) = vertices[i]
            val (lat2, lng2) = vertices[(i - 1 + vertices.size) % vertices.size]
            if ((lng1 > point.lng) != (lng2 > point.lng) &&
                point.lat < (lat2 - lat1) * (point.lng - lng1) / (lng2 - lng1) + lat1
            ) inside = !inside
        }

        return inside to if (inside) polygon.addressId else null
    }

    private suspend fun suspendGetTariffs(addressId: Int): Result<GetTariffsModel> =
        supervisorScope {
            val from = uiState.value.selectedLocation?.point?.toPair()
                ?: return@supervisorScope Result.failure(Exception("No point"))
            val to = uiState.value.destinations.mapNotNull { it.point?.toPair() }
            val optionIds = uiState.value.selectedOptions.map { it.id }

            val result = getTariffsUseCase(
                addressId = addressId,
                optionIds = optionIds,
                coords = listOf(from) + to
            )
            result.fold(
                onSuccess = ::handleTariffsSuccess,
                onFailure = {
                    handleTariffsFailure()
                }
            )
            result
        }

    private fun handleTariffsSuccess(model: GetTariffsModel) = _uiState.update {
        val selected = determineSelectedTariff(it.selectedTariff, model.tariff)
        it.copy(
            tariffs = model,
            selectedTariff = selected,
            options = selected?.services ?: emptyList(),
            selectedOptions = if (it.selectedTariff?.id != selected?.id) emptyList() else it.selectedOptions
        )
    }

    private fun determineSelectedTariff(
        current: GetTariffsModel.Tariff?,
        all: List<GetTariffsModel.Tariff>
    ) = when {
        all.isEmpty() -> null
        current == null -> all.firstOrNull()
        all.none { it.id == current.id } -> all.firstOrNull()
        else -> all.find { it.id == current.id } ?: all.firstOrNull()
    }

    private fun handleTariffsFailure() {
        _uiState.update {
            it.copy(
                tariffs = null,
                selectedTariff = null,
                options = emptyList(),
                selectedOptions = emptyList()
            )
        }
    }

    private fun getTariffs(addressId: Int) = viewModelScope.launch {
        suspendGetTariffs(addressId)
    }

    private fun getTimeout(point: MapPoint) = viewModelScope.launch {
        uiState.value.selectedTariff?.id?.let {
            getTimeOutUseCase(point.lat, point.lng, it).onSuccess { result ->
                _uiState.update { state ->
                    state.copy(
                        timeout = result.timeout,
                        drivers = result.executors
                    )
                }
            }
        }
    }

    private fun getShowOrder(id: Int) = viewModelScope.launch {
        getShowOrderUseCase(id).onSuccess {
            MainSheetChannel.intentChannel.emit(OrderTaxiSheetIntent.OrderCreated(it))
            _uiState.update { s -> s.copy(order = it, loading = false) }
        }.onFailure {
            _uiState.update { s -> s.copy(loading = false) }
        }
    }

    private fun orderTaxi() {
        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            uiState.value.mapToOrderTaxiDto()?.let {
                orderTaxiUseCase(it)
                    .onSuccess { o ->
                        _uiState.update { s ->
                            s.copy(
                                orderId = o.orderId,
                                bonusAmount = 0,
                                isBonusEnabled = false
                            )
                        }
                    }
                    .onFailure { _uiState.update { s -> s.copy(loading = false) } }
            }
        }
    }

    private fun updatePaymentType(type: PaymentType) {
        viewModelScope.launch {
            prefs.setPaymentType(type)
        }
    }

    private fun MapPoint.toPair() = lat to lng

    fun getPolygon() = viewModelScope.launch {
        if (_isPolygonLoading.value) return@launch
        _isPolygonLoading.value = true
        try {
            getPolygonUseCase().onSuccess {
                _uiState.update { state -> state.copy(polygon = it) }
            }
        } finally {
            _isPolygonLoading.value = false
        }
    }

    fun getCardList() = viewModelScope.launch {
        getCardListUseCase().onSuccess { res -> _uiState.update { it.copy(cardList = res) } }
    }

    private fun setDestination(dest: List<Destination>) {
        _uiState.update { it.copy(destinations = dest) }
        refreshTariffsIfPossible()
    }

    fun setPaymentType(paymentType: PaymentType) =
        _uiState.update { it.copy(selectedPaymentType = paymentType) }

    fun setSearchByNameSheetVisibility(value: SearchByNameSheetValue) =
        _uiState.update { it.copy(isSearchByNameSheetVisible = value) }

    fun setSelectFromMapViewVisibility(value: SelectFromMapViewValue) =
        _uiState.update { it.copy(selectFromMapViewVisibility = value) }

    fun setAddDestinationSheetVisibility(value: Boolean) =
        _uiState.update { it.copy(isAddDestinationSheetVisible = value) }

    fun setArrangeDestinationsSheetVisibility(value: Boolean) =
        _uiState.update { it.copy(isArrangeDestinationsSheetVisible = value) }

    private fun setBonusAmountSheetVisibility(value: Boolean) =
        _uiState.update { it.copy(isSetBonusAmountBottomSheetVisible = value) }

    fun setFooterHeight(height: Dp) = updateSheetHeight { it.copy(footerHeight = height) }
    fun setSheetHeight(height: Dp) = updateSheetHeight { it.copy(sheetHeight = height) }

    private fun updateSheetHeight(update: (MainSheetState) -> MainSheetState) {
        _uiState.update(update)
        SheetCoordinator.updateSheetHeight(
            MAIN_SHEET_ROUTE,
            uiState.value.footerHeight + uiState.value.sheetHeight
        )
    }

    fun setBonusAmount(amount: Int) {
        setBonusAmountSheetVisibility(false)
        _uiState.update {
            it.copy(
                bonusAmount = amount,
                isBonusEnabled = amount > 0
            )
        }
    }

    fun setBonusInfoVisibility(value: Boolean) =
        _uiState.update { it.copy(isBonusInfoSheetVisibility = value) }
}