package uz.yalla.client.feature.order.presentation.no_service.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapViewValue
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.feature.order.domain.usecase.tariff.GetTariffsUseCase
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceSheetAction
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceSheetChannel
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceSheetIntent

class NoServiceViewModel(
    private val getTariffsUseCase: GetTariffsUseCase,
    private val prefs: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoServiceState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            NoServiceSheetChannel.actionFlow.collect { action ->
                when (action) {
                    is NoServiceSheetAction.SetLocation -> setSelectedLocation(action.location)
                }
            }
        }
    }

    fun setSelectAddressVisibility(isVisible: Boolean) =
        _uiState.update { it.copy(setLocationSheetVisibility = isVisible) }

    fun setSelectFromMapVisibility(value: SelectFromMapViewValue) =
        _uiState.update { it.copy(selectFromMapVisibility = value) }

    fun onIntent(intent: NoServiceSheetIntent) {
        viewModelScope.launch { NoServiceSheetChannel.sendIntent(intent) }
    }

    private fun setSelectedLocation(location: Location) {
        viewModelScope.launch {
            val resolved = location.withResolvedPoint(
                fallback = prefs.entryLocation.first().toMapPointOrNull()
            )
            _uiState.update { it.copy(location = resolved) }
            resolved.point?.let { tryFetchTariffs(it) }
        }
    }

    private suspend fun tryFetchTariffs(from: MapPoint) {
        runCatching {
            getTariffsUseCase(optionIds = emptyList(), coords = listOf(from.lat to from.lng))
        }.getOrNull()
            ?.getOrNull()
            ?.let {
                NoServiceSheetChannel.sendIntent(NoServiceSheetIntent.SetServiceState(it.working.isWorking))
            }
    }


    private fun Pair<Double, Double>.toMapPointOrNull(): MapPoint? =
        takeUnless { first == 0.0 && second == 0.0 }?.let { (lat, lng) -> MapPoint(lat, lng) }

    private fun Location.withResolvedPoint(fallback: MapPoint?): Location =
        if (this.point != null) this else copy(point = fallback)

    private fun GetTariffsModel.isWorkingAvailable(): Boolean = working.isWorking
}