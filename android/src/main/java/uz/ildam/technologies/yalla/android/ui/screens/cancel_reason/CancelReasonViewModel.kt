package uz.ildam.technologies.yalla.android.ui.screens.cancel_reason

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.SettingModel
import uz.ildam.technologies.yalla.feature.order.domain.usecase.order.CancelReasonUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.order.GetSettingUseCase

class CancelReasonViewModel(
    private val getSettingUseCase: GetSettingUseCase,
    private val cancelReasonUseCase: CancelReasonUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CancelReasonUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<CancelReasonActionState>()
    val actionState = _actionState.asSharedFlow()

    fun getSetting() = viewModelScope.launch {
        _actionState.emit(CancelReasonActionState.Loading)
        getSettingUseCase()
            .onSuccess {result ->
                _uiState.update { it.copy(reasons = result.reasons) }
                _actionState.emit(CancelReasonActionState.GettingSuccess)
            }
            .onFailure {
                _actionState.emit(CancelReasonActionState.Error)
            }
    }

    fun cancelReason() = viewModelScope.launch {
        if (AppPreferences.lastOrderId != -1) {
            _actionState.emit(CancelReasonActionState.Loading)
            uiState.value.selectedReason?.apply {
                cancelReasonUseCase(
                    orderId = AppPreferences.lastOrderId,
                    reasonId = id.toInt(),
                    reasonComment = name
                ).onSuccess { _actionState.emit(CancelReasonActionState.SettingSuccess) }
                    .onFailure { _actionState.emit(CancelReasonActionState.Error) }
            }
        }
        AppPreferences.lastOrderId = -1
    }

    fun updateSelectedReason(reason: SettingModel.CancelReason) =
        _uiState.update { it.copy(selectedReason = reason) }
}