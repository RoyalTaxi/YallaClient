package uz.yalla.client.feature.order.presentation.search.model

import androidx.compose.ui.unit.Dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.order.domain.usecase.order.CancelRideUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetSettingUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.order.domain.usecase.order.OrderFasterUseCase
import uz.yalla.client.feature.order.domain.usecase.tariff.GetTimeOutUseCase
import uz.yalla.client.feature.order.presentation.components.dialog.ConfirmationDialogEvent
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetChannel
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetIntent
import kotlin.time.Duration.Companion.seconds

class SearchCarSheetViewModel(
    private val cancelRideUseCase: CancelRideUseCase,
    private val getTimeOutUseCase: GetTimeOutUseCase,
    private val getSettingUseCase: GetSettingUseCase,
    private val getShowOrderUseCase: GetShowOrderUseCase,
    private val orderFasterUseCase: OrderFasterUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchCarSheetState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            uiState
                .distinctUntilChangedBy { Pair(it.searchingAddressPoint, it.tariffId) }
                .collectLatest { state ->
                    while (isActive) {
                        state.searchingAddressPoint?.let { point ->
                            state.tariffId?.let { tariffId ->
                                getTimeout(point = point, tariffId = tariffId)
                            }
                        }
                        delay(5.seconds)
                    }
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            uiState
                .distinctUntilChangedBy { it.selectedOrder }
                .collectLatest { state ->
                    if (state.selectedOrder?.status == OrderStatus.New && state.isFasterEnabled == null) {
                        _uiState.update { it.copy(isFasterEnabled = true) }
                    }
                }

        }
    }

    fun onIntent(intent: SearchCarSheetIntent) {
        viewModelScope.launch {
            when (intent) {
                is SearchCarSheetIntent.SetFooterHeight -> setFooterHeight(intent.height)
                is SearchCarSheetIntent.SetHeaderHeight -> setHeaderHeight(intent.height)
                else -> {
                    SearchCarSheetChannel.sendIntent(intent)
                }
            }
        }
    }

    private fun getTimeout(point: MapPoint, tariffId: Int) {
        viewModelScope.launch {
            getTimeOutUseCase(point.lat, point.lng, tariffId).onSuccess { data ->
                _uiState.update {
                    it.copy(
                        timeout = data.timeout.takeIf { timeout -> timeout != 0 } ?: 1
                    )
                }
            }
        }
    }

    private fun getOrderDetails() {
        val orderId = uiState.value.orderId ?: return
        _uiState.update { it.copy(isOrderCancellable = false) }

        viewModelScope.launch {
            getShowOrderUseCase(orderId).onSuccess { data ->
                _uiState.update {
                    it.copy(
                        selectedOrder = data,
                        isOrderCancellable = data.status in OrderStatus.cancellable
                    )
                }
            }
        }
    }

    fun orderFaster() {
        val orderId = uiState.value.orderId ?: return
        _uiState.update { it.copy(isFasterEnabled = true) }
        viewModelScope.launch {
            orderFasterUseCase(orderId)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            dialogEvent = ConfirmationDialogEvent.Success,
                            isFasterEnabled = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            dialogEvent = ConfirmationDialogEvent.Error,
                            isFasterEnabled = true
                        )
                    }
                }
        }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(dialogEvent = ConfirmationDialogEvent.Invisible) }
    }

    fun getSetting() {
        viewModelScope.launch {
            getSettingUseCase().onSuccess { setting ->
                _uiState.update { it.copy(setting = setting) }
            }
        }
    }

    fun cancelRide() {
        val orderId = uiState.value.orderId
        viewModelScope.launch {
            if (orderId != null) {
                cancelRideUseCase(orderId)
            }
        }.invokeOnCompletion {
            onIntent(SearchCarSheetIntent.OnCancelled(orderId))
        }
    }

    fun setDetailsBottomSheetVisibility(isVisible: Boolean) {
        _uiState.update { it.copy(detailsBottomSheetVisibility = isVisible) }
    }

    fun setCancelBottomSheetVisibility(isVisible: Boolean) {
        if (isVisible) getOrderDetails()
        _uiState.update { it.copy(cancelBottomSheetVisibility = isVisible) }
    }

    fun setPoint(point: MapPoint) {
        _uiState.update { it.copy(searchingAddressPoint = point) }
    }

    fun setTariffId(tariffId: Int) {
        _uiState.update { it.copy(tariffId = tariffId) }
    }

    fun setOrderId(orderId: Int) {
        _uiState.update { it.copy(orderId = orderId) }
        getOrderDetails()
    }

    private fun setHeaderHeight(height: Dp) {
        _uiState.update { it.copy(headerHeight = height) }
    }

    private fun setFooterHeight(height: Dp) {
        _uiState.update { it.copy(footerHeight = height) }
    }

    public override fun onCleared() {
        super.onCleared()
    }
}
