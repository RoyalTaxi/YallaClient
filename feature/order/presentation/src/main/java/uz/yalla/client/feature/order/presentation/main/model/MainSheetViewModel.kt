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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.data.enums.PaymentType
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.core.data.mapper.orFalse
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.SelectedLocation
import uz.yalla.client.feature.map.domain.model.response.PolygonRemoteItem
import uz.yalla.client.feature.map.domain.usecase.GetPolygonUseCase
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.feature.order.domain.usecase.order.OrderTaxiUseCase
import uz.yalla.client.feature.order.domain.usecase.tariff.GetTariffsUseCase
import uz.yalla.client.feature.order.presentation.main.view.MainBottomSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainBottomSheetIntent.FooterIntent
import uz.yalla.client.feature.order.presentation.main.view.MainBottomSheetIntent.OrderTaxiBottomSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainBottomSheetIntent.TariffInfoBottomSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainBottomSheetIntent.PaymentMethodSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheet
import uz.yalla.client.feature.payment.domain.usecase.GetCardListUseCase

class MainSheetViewModel(
    private val getPolygonUseCase: GetPolygonUseCase,
    private val getTariffsUseCase: GetTariffsUseCase,
    private val orderTaxiUseCase: OrderTaxiUseCase,
    private val getCardListUseCase: GetCardListUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainSheetState())
    val uiState: StateFlow<MainSheetState> = _uiState.asStateFlow()

    private val _sheetVisibilityListener: Channel<Unit> = Channel(Channel.BUFFERED)
    val sheetVisibilityListener = _sheetVisibilityListener.receiveAsFlow()

    val buttonAndOptionsState = uiState
        .map { state ->
            val isTariffValidWithOptions = state.selectedOptions.all { selected ->
                state.options.any { option ->
                    selected.cost == option.cost && selected.name == option.name
                }
            }

            val isButtonEnabled = !state.loading &&
                    state.selectedTariff != null &&
                    isTariffValidWithOptions &&
                    !(state.selectedTariff.isSecondAddressMandatory && state.destinations.isNotEmpty())

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
                .distinctUntilChangedBy { it.selectedLocation }
                .collectLatest { state ->
                    state.selectedLocation?.point?.let { point ->
                        val (isInsidePolygon, addressId) = isPointInsidePolygon(point)
                        if (isInsidePolygon) {
                            getTariffs(addressId)
                        } else {
                            setSelectedTariff(null)
                            handleTariffsFailure()
                        }
                    }
                }
        }
    }

    fun onIntent(intent: MainBottomSheetIntent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (intent) {
                is OrderTaxiBottomSheetIntent.SelectTariff -> {
                    if (intent.wasSelected) {
                        _sheetVisibilityListener.send(Unit)
                    } else {
                        setSelectedTariff(intent.tariff)
                    }
                }

                is OrderTaxiBottomSheetIntent.SetSheetHeight -> setSheetHeight(intent.height)
                is TariffInfoBottomSheetIntent.ClearOptions -> setSelectedOptions(emptyList())
                is TariffInfoBottomSheetIntent.OptionsChange -> setSelectedOptions(intent.options)
                is TariffInfoBottomSheetIntent.ChangeShadowVisibility -> {
                    _uiState.update { it.copy(shadowVisibility = intent.visible) }
                }

                is FooterIntent.SetFooterHeight -> setFooterHeight(intent.height)
                is FooterIntent.ClearOptions -> setSelectedOptions(emptyList())
                is FooterIntent.CreateOrder -> orderTaxi()
                is FooterIntent.ClickPaymentButton -> {
                    _uiState.update { it.copy(isPaymentMethodSheetVisible = true) }
                }
                is PaymentMethodSheetIntent.OnDismissRequest -> {
                    _uiState.update { it.copy(isPaymentMethodSheetVisible = false) }
                }
                is PaymentMethodSheetIntent.OnSelectPaymentType -> {
                    AppPreferences.paymentType = intent.paymentType
                    setPaymentType(intent.paymentType)
                }

                is FooterIntent.ChangeSheetVisibility -> _sheetVisibilityListener.send(Unit)
                else -> MainSheet.mutableIntentFlow.emit(intent)
            }
        }
    }

    fun getPolygon() {
        viewModelScope.launch(Dispatchers.IO) {
            getPolygonUseCase().onSuccess { polygon ->
                _uiState.update { it.copy(polygon = polygon) }
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
        _uiState.update { it.copy(selectedLocation = selectedLocation) }
    }

    fun setDestination(destinations: List<Destination>) {
        _uiState.update { it.copy(destinations = destinations) }
    }

    fun setPaymentType(type: PaymentType) {
        _uiState.update { it.copy(selectedPaymentType = type) }
    }

    fun setLoading(loading: Boolean) {
        _uiState.update { it.copy(loading = loading) }
        if (loading) {
            _uiState.update { it.copy(selectedLocation = null) }
        }
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

    private fun setSelectedOptions(options: List<GetTariffsModel.Tariff.Service>) {
        _uiState.update { it.copy(selectedOptions = options) }
    }

    private fun setFooterHeight(height: Dp) {
        _uiState.update { it.copy(footerHeight = height) }
        viewModelScope.launch(Dispatchers.Main) {
            MainSheet.mutableIntentFlow.emit(
                OrderTaxiBottomSheetIntent.SetSheetHeight(height = height + uiState.value.sheetHeight)
            )
        }
    }

    private fun setSheetHeight(height: Dp) {
        _uiState.update { it.copy(sheetHeight = height) }
        viewModelScope.launch(Dispatchers.Main) {
            MainSheet.mutableIntentFlow.emit(
                OrderTaxiBottomSheetIntent.SetSheetHeight(height = height + uiState.value.footerHeight)
            )
        }
    }

    private suspend fun isPointInsidePolygon(point: MapPoint): Pair<Boolean, Int> = coroutineScope {
        val results = uiState.value.polygon.map { polygonItem ->
            async(Dispatchers.Default) {
                isPointInPolygon(point, polygonItem)
            }
        }

        results.awaitAll().firstOrNull { it.first } ?: Pair(false, 0)
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
                onSuccess = { tariffsModel -> handleTariffsSuccess(tariffsModel) },
                onFailure = { handleTariffsFailure() }
            )
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
                    currentState.selectedOptions,
                loading = false
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
            else -> currentTariff
        }
    }

    private fun handleTariffsFailure() {
        _uiState.update {
            it.copy(
                tariffs = null,
                selectedTariff = null,
                options = emptyList(),
                selectedOptions = emptyList(),
                loading = false
            )
        }
    }

    private fun orderTaxi() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState.value.mapToOrderTaxiDto()?.let { model ->
                orderTaxiUseCase(model).onSuccess { order ->
                    _uiState.update { it.copy(orderId = order.orderId) }
                }
            }
        }
    }

    private fun MapPoint.toPair() = Pair(lat, lng)
}