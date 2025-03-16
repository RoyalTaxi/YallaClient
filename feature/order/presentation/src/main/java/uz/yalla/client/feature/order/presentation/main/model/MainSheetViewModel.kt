package uz.yalla.client.feature.order.presentation.main.model

import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.data.enums.PaymentType
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.SelectedLocation
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.feature.order.domain.usecase.order.OrderTaxiUseCase
import uz.yalla.client.feature.order.domain.usecase.tariff.GetTariffsUseCase
import uz.yalla.client.feature.order.presentation.main.view.MainBottomSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainBottomSheetIntent.FooterIntent
import uz.yalla.client.feature.order.presentation.main.view.MainBottomSheetIntent.OrderTaxiBottomSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainBottomSheetIntent.TariffInfoBottomSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheet.mutableIntentFlow
import uz.yalla.client.feature.payment.domain.usecase.GetCardListUseCase

class MainSheetViewModel(
    private val getTariffsUseCase: GetTariffsUseCase,
    private val orderTaxiUseCase: OrderTaxiUseCase,
    private val getCardListUseCase: GetCardListUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainSheetState())
    val uiState = _uiState.asStateFlow()


    val isButtonEnabled = uiState
        .map { state ->
            state.loading.not() && state.selectedTariff != null &&
                    isTariffValidWithOptions.value &&
                    !(state.selectedTariff.secondAddress && state.destinations.isNotEmpty())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = false
        )

    val isTariffValidWithOptions = uiState
        .map { state ->
            state.selectedOptions.all { selected ->
                state.options.any { tariff ->
                    selected.cost == tariff.cost && selected.name == tariff.name
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = true
        )

    fun onIntent(intent: MainBottomSheetIntent) {
        when (intent) {
            is OrderTaxiBottomSheetIntent.SelectTariff -> setSelectedTariff(intent.tariff)
            is OrderTaxiBottomSheetIntent.SetSheetHeight -> setSheetHeight(intent.height)

            is TariffInfoBottomSheetIntent.ClearOptions -> setSelectedOptions(emptyList())
            is TariffInfoBottomSheetIntent.OptionsChange -> setSelectedOptions(intent.options)

            is FooterIntent.ClearOptions -> setSelectedOptions(emptyList())
            is FooterIntent.CreateOrder -> orderTaxi()
            is FooterIntent.SetFooterHeight -> setFooterHeight(intent.height)

            is FooterIntent.ClickPaymentButton -> {}
            is FooterIntent.ChangeSheetVisibility -> {}

            else -> viewModelScope.launch { mutableIntentFlow.emit(intent) }
        }
    }

    private fun getTariffs() = viewModelScope.launch {
        val addressId = uiState.value.selectedLocation?.addressId ?: return@launch
        val from = uiState.value.selectedLocation?.point?.toPair() ?: return@launch
        val to = uiState.value.destinations.mapNotNull { it.point?.toPair() }
        val selectedOptionsIds = uiState.value.selectedOptions.map { it.id }
        getTariffsUseCase(
            addressId = addressId,
            optionIds = selectedOptionsIds,
            coords = listOf(from, *to.toTypedArray())
        ).onSuccess { tariffs ->
            _uiState.update { it.copy(tariffs = tariffs) }
        }.onFailure {
            _uiState.update { it.copy(tariffs = null) }
        }
    }

    private fun orderTaxi() = viewModelScope.launch {
        uiState.value.mapToOrderTaxiDto()?.let { model ->
            orderTaxiUseCase(model).onSuccess { order ->
                _uiState.update { it.copy(orderId = order.orderId) }
            }
        }
    }

    private fun getCardList() = viewModelScope.launch {
        getCardListUseCase().onSuccess { cardList ->
            _uiState.update { it.copy(cardList = cardList) }
        }
    }

    private fun setSelectedTariff(tariff: GetTariffsModel.Tariff) {
        _uiState.update { it.copy(selectedTariff = tariff) }
    }

    private fun setSelectedOptions(options: List<GetTariffsModel.Tariff.Service>) {
        _uiState.update { it.copy(selectedOptions = options) }
    }

    fun setLoading(loading: Boolean) {
        _uiState.update { it.copy(loading = loading) }
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

    private fun setSheetHeight(sheetHeight: Dp) {
        _uiState.update { it.copy(sheetHeight = sheetHeight) }
    }

    private fun setFooterHeight(footerHeight: Dp) {
        _uiState.update { it.copy(footerHeight = footerHeight) }
    }


    private fun MapPoint.toPair() = Pair(lat, lng)
}