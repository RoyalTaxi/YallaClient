package uz.ildam.technologies.yalla.android.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.map.domain.model.map.PolygonRemoteItem
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.GetAddressNameUseCase
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.GetPolygonUseCase
import uz.ildam.technologies.yalla.feature.order.domain.model.tarrif.GetTariffsModel
import uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff.GetTariffsUseCase
import uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff.GetTimeOutUseCase

class MapViewModel(
    private val getPolygonUseCase: GetPolygonUseCase,
    private val getAddressNameUseCase: GetAddressNameUseCase,
    private val getTariffsUseCase: GetTariffsUseCase,
    private val getTimeOutUseCase: GetTimeOutUseCase,
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

            is Result.Error -> updateStateToNotFound()
        }
    }

    fun getAddressDetails(point: LatLng) = viewModelScope.launch {
        _actionState.emit(MapActionState.LoadingAddressId)
        if (addresses.isEmpty()) fetchPolygons()
        else addresses.firstOrNull {
            isPointInsidePolygon(
                point = point,
                vertices = it.polygons.map { polygon ->
                    Pair(polygon.lat, polygon.lng)
                }
            )
        }?.let { address ->
            _uiState.update { it.copy(selectedAddressId = address.addressId) }
            _actionState.emit(MapActionState.AddressIdLoaded(address.addressId))
        } ?: run {
            _uiState.update { it.copy(selectedAddressId = null) }
        }
        fetchAddressName(point)
    }

    private suspend fun fetchAddressName(point: LatLng) {
        _actionState.emit(MapActionState.LoadingAddressName)
        when (val result = getAddressNameUseCase(point.latitude, point.longitude)) {
            is Result.Success -> _actionState.emit(MapActionState.AddressNameLoaded(result.data.name))
            is Result.Error -> updateStateToNotFound()
        }
    }

    fun getTimeout(point: LatLng) = viewModelScope.launch {
        val selectedTariff = _uiState.value.selectedTariff
            ?: _uiState.value.tariffs?.tariff?.firstOrNull()

        selectedTariff?.let { tariff ->
            _actionState.emit(MapActionState.LoadingTariffs)
            when (val result = getTimeOutUseCase(
                lat = point.latitude,
                lng = point.longitude,
                tariffId = tariff.id
            )) {
                is Result.Error -> updateStateToNotFound()
                is Result.Success -> _uiState.update { it.copy(timeout = result.data.timeout) }
            }
        }
    }

    fun fetchTariffs(addressId: Int, from: LatLng, to: LatLng? = null) = viewModelScope.launch {
        _actionState.emit(MapActionState.LoadingTariffs)
        val coordinates = listOfNotNull(from.toPair(), to?.toPair())
        when (val result = getTariffsUseCase(listOf(), coordinates, addressId)) {
            is Result.Success -> {
                _actionState.emit(MapActionState.TariffsLoaded)
                updateTariffs(result.data)
            }

            is Result.Error -> updateStateToNotFound()
        }
    }

    private fun updateTariffs(data: GetTariffsModel) {
        _uiState.update { it.copy(tariffs = data) }
        val selectedTariff = _uiState.value.selectedTariff
        if (selectedTariff?.id !in data.tariff.map { it.id }) updateUIState(selectedTariff = data.tariff.firstOrNull())
    }

    private fun isPointInsidePolygon(point: LatLng, vertices: List<Pair<Double, Double>>): Boolean {
        var isInside = false
        for (i in vertices.indices) {
            val j = if (i == 0) vertices.size - 1 else i - 1
            val (lat1, lng1) = vertices[i]
            val (lat2, lng2) = vertices[j]
            val intersects = (lng1 > point.longitude) != (lng2 > point.longitude) &&
                    (point.latitude < (lat2 - lat1) * (point.longitude - lng1) / (lng2 - lng1) + lat1)
            if (intersects) isInside = !isInside
        }
        return isInside
    }

    private fun updateStateToNotFound() {
        updateUIState(selectedAddressName = null)
        updateUIState(selectedAddressId = null)
        _uiState.update { it.copy(tariffs = null) }
    }

    private fun LatLng.toPair() = Pair(latitude, longitude)

    fun changeStateToNotFound() {
        updateUIState(
            selectedAddressName = null,
            selectedAddressId = null,
            tariffs = null
        )
    }

    fun updateUIState(
        timeout: Int? = _uiState.value.timeout,
        selectedAddressName: String? = _uiState.value.selectedAddressName,
        selectedAddressId: Int? = _uiState.value.selectedAddressId,
        selectedTariff: GetTariffsModel.Tariff? = _uiState.value.selectedTariff,
        tariffs: GetTariffsModel? = _uiState.value.tariffs
    ) {
        _uiState.update {
            it.copy(
                timeout = timeout,
                selectedAddressName = selectedAddressName,
                selectedAddressId = selectedAddressId,
                selectedTariff = selectedTariff,
                tariffs = tariffs
            )
        }
    }
}