package uz.yalla.client.feature.order.presentation.main.model

import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import uz.yalla.client.core.common.sheet.search_address.SearchByNameSheetValue
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapViewValue
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.data.mapper.orFalse
import uz.yalla.client.core.domain.error.DataError
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.*
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
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.*
import uz.yalla.client.feature.payment.domain.usecase.GetCardListUseCase

class MainSheetViewModel(
    private val getTariffsUseCase: GetTariffsUseCase,
    private val orderTaxiUseCase: OrderTaxiUseCase,
    private val getCardListUseCase: GetCardListUseCase,
    private val getTimeOutUseCase: GetTimeOutUseCase,
    private val getShowOrderUseCase: GetShowOrderUseCase,
    private val prefs: AppPreferences
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(MainSheetState())
    val uiState = _uiState.asStateFlow()

    private val _sheetVisibilityListener = Channel<Unit>(Channel.BUFFERED)
    val sheetVisibilityListener = _sheetVisibilityListener.receiveAsFlow()

    val buttonAndOptionsState = uiState
        .map(::mapToButtonAndOptionsState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ButtonAndOptionsState()
        )

    fun updateTimeout() {
        uiState.value.run {
            location?.point?.takeIf { selectedTariff != null }?.let {
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
            MainSheetChannel.actionFlow.collectLatest { action ->
                when (action) {
                    is MainSheetAction.SetDestination -> setDestination(action.destinations)
                    is MainSheetAction.SetLocation -> setSelectedLocation(action.location)
                    is MainSheetAction.SetBonusInfoVisibility -> setBonusInfoVisibility(action.visible)
                }
            }
        }
    }

    fun initializeLocation() {
        viewModelScope.launch {
            // Only initialize if no location is currently set
            if (uiState.value.location == null) {
                val entryLocation = prefs.entryLocation.first()
                if (entryLocation.first != 0.0 && entryLocation.second != 0.0) {
                    val location = Location(
                        name = null,
                        addressId = null,
                        point = MapPoint(entryLocation.first, entryLocation.second)
                    )
                    setSelectedLocation(location)
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

                else -> MainSheetChannel.sendIntent(intent)
            }
        }
    }

    private fun mapToButtonAndOptionsState(state: MainSheetState): ButtonAndOptionsState {
        val isTariffValid =
            state.selectedOptions.all { opt -> state.options.any { it.name == opt.name && it.cost == opt.cost } }
        val hasTariffs = state.tariffs?.tariff?.isNotEmpty() == true
        val isButtonEnabled =
            hasTariffs &&
                    state.selectedTariff != null &&
                    isTariffValid &&
                    (!state.selectedTariff.isSecondAddressMandatory || state.destinations.isNotEmpty())
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
        uiState.value.selectedLocationId?.let {
            viewModelScope.launch {
                suspendGetTariffs()
            }
        }
    }

    fun observeTimeoutAndDrivers() {
        viewModelScope.launch {
            uiState.distinctUntilChangedBy { it.drivers to it.timeout }
                .collectLatest {
                    MainSheetChannel.sendIntent(
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
        if (intent.wasSelected) _sheetVisibilityListener.trySend(Unit)
        else {
            setSelectedTariff(intent.tariff)
            uiState.value.selectedLocationId?.let {
                viewModelScope.launch {
                    suspendGetTariffs()
                }
            }
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

    private fun setSelectedLocation(location: Location) {
        viewModelScope.launch {
            val fallback = prefs.entryLocation.first()
            val point = location.point
                ?: fallback.takeIf { it.first != 0.0 && it.second != 0.0 }
                    ?.let { MapPoint(it.first, it.second) }

            val updated = location.copy(point = point)
            _uiState.update { it.copy(location = updated) }
            point?.let { suspendGetTariffs() }
        }
    }

    private suspend fun suspendGetTariffs(): Result<GetTariffsModel> =
        supervisorScope {
            val from = uiState.value.location?.point?.toPair()
                ?: return@supervisorScope Result.failure(Exception("No point"))
            val to = uiState.value.destinations.mapNotNull { it.point?.toPair() }
            val optionIds = uiState.value.selectedOptions.map { it.id }

            val result = getTariffsUseCase(
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

    private fun handleTariffsSuccess(model: GetTariffsModel) {
        _uiState.update {
            val selected = determineSelectedTariff(it.selectedTariff, model.tariff)
            it.copy(
                tariffs = model,
                selectedLocationId = model.working.addressId,
                selectedTariff = selected,
                options = selected?.services ?: emptyList(),
                selectedOptions = if (it.selectedTariff?.id != selected?.id) emptyList() else it.selectedOptions,
            )
        }

        if (model.working.isWorking) {
            MainSheetChannel.sendIntent(OrderTaxiSheetIntent.SetServiceState(true))
        } else {
            setSelectedTariff(null)
            handleTariffsFailure()
            MainSheetChannel.sendIntent(OrderTaxiSheetIntent.SetServiceState(false))
        }
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
                selectedOptions = emptyList(),
                selectedService = "road"
            )
        }
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
            MainSheetChannel.sendIntent(OrderTaxiSheetIntent.OrderCreated(it))
            _uiState.update { s -> s.copy(order = it) }
        }
    }

    private fun orderTaxi() {
        viewModelScope.launchWithLoading {
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
                    .onFailure {throwable ->
                        when (throwable) {
                            DataError.Network.NOT_SUFFICIENT_BALANCE -> {
                                setNotSufficientBalanceDialogVisibility(true)
                            }
                            else -> {
                                handleException(throwable)
                            }
                        }
                    }
            }
        }
    }

    private fun updatePaymentType(type: PaymentType) {
        viewModelScope.launch {
            prefs.setPaymentType(type)
        }
    }

    private fun MapPoint.toPair() = lat to lng


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

    fun setBonusAmount(amount: Long) {
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

    fun setNotSufficientBalanceDialogVisibility(value: Boolean) =
        _uiState.update { it.copy(isNotSufficientBalanceDialogVisibility = value) }
}
