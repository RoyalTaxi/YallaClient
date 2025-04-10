package uz.yalla.client.feature.order.presentation.cancel_reason.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.feature.order.domain.model.response.order.SettingModel
import uz.yalla.client.feature.order.domain.usecase.order.CancelReasonUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetSettingUseCase

class CancelReasonViewModel(
    private val getSettingUseCase: GetSettingUseCase,
    private val cancelReasonUseCase: CancelReasonUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CancelReasonUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<CancelReasonActionState>(replay = 1)
    val actionState = _actionState.asSharedFlow()

    init {
        loadReasons()
    }

    private fun loadReasons() {
        viewModelScope.launch {
            if (uiState.value.reasons.isEmpty()) {
                getSetting()
            } else {
                _actionState.emit(CancelReasonActionState.GettingSuccess)
            }
        }
    }

    private fun getSetting() {
        viewModelScope.launch {
            _actionState.emit(CancelReasonActionState.Loading)

            try {
                val result = getSettingUseCase()
                result.onSuccess { settingResult ->
                    _uiState.update { it.copy(reasons = settingResult.reasons) }
                    _actionState.emit(CancelReasonActionState.GettingSuccess)
                }.onFailure { error ->
                    _actionState.emit(CancelReasonActionState.Error(error.message))
                }
            } catch (e: Exception) {
                _actionState.emit(CancelReasonActionState.Error(e.message))
            }
        }
    }

    fun cancelReason(orderId: Int) {
        viewModelScope.launch {
            uiState.value.selectedReason?.let { reason ->
                _actionState.emit(CancelReasonActionState.Loading)

                try {
                    cancelReasonUseCase(
                        orderId = orderId,
                        reasonId = reason.id,
                        reasonComment = reason.name
                    ).onSuccess {
                        _actionState.emit(CancelReasonActionState.SettingSuccess)
                    }.onFailure { error ->
                        _actionState.emit(CancelReasonActionState.Error(error.message))
                    }
                } catch (e: Exception) {
                    _actionState.emit(CancelReasonActionState.Error(e.message))
                }
            } ?: run {
                _actionState.emit(CancelReasonActionState.Error("No reason selected"))
            }
        }
    }

    fun updateSelectedReason(reason: SettingModel.CancelReason) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedReason = reason) }
        }
    }
}