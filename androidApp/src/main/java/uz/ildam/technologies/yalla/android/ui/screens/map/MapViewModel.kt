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
import uz.ildam.technologies.yalla.feature.order.domain.usecase.tariff.GetTariffsUseCase

class MapViewModel(
    private val getPolygonUseCase: GetPolygonUseCase,
    private val getAddressNameUseCase: GetAddressNameUseCase,
    private val getTariffsUseCase: GetTariffsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapUIState())
    val uiState = _uiState.asStateFlow()

    private val _actionState = MutableSharedFlow<MapActionState>()
    val actionState = _actionState.asSharedFlow()

    private var addresses = listOf<PolygonRemoteItem>()

    fun getPolygon() = viewModelScope.launch {
        when (val result = getPolygonUseCase()) {
            is Result.Error -> {}

            is Result.Success -> {
                addresses = result.data
            }
        }
    }

    fun getAddressId(point: LatLng) = viewModelScope.launch {
        _actionState.emit(MapActionState.LoadingAddressId)
        for (address in addresses) {
            val polygonVertices = address.polygons
            var isInsidePolygon = false

            for (currentVertexIndex in polygonVertices.indices) {
                val previousVertexIndex =
                    if (currentVertexIndex == 0) polygonVertices.size - 1 else currentVertexIndex - 1
                val (currentLat, currentLng) = polygonVertices[currentVertexIndex]
                val (prevLat, prevLng) = polygonVertices[previousVertexIndex]

                val isIntersecting =
                    (currentLng > point.longitude) != (prevLng > point.longitude) &&
                            (point.latitude < (prevLat - currentLat) * (point.longitude - currentLng) / (prevLng - currentLng) + currentLat)
                if (isIntersecting) isInsidePolygon = !isInsidePolygon
            }

            if (isInsidePolygon) {
                _uiState.update { it.copy(selectedAddressId = address.addressId) }
                _actionState.emit(MapActionState.AddressIdLoaded(address.addressId))
            }
        }
    }

    fun getAddressName(point: LatLng) = viewModelScope.launch {
        _actionState.emit(MapActionState.LoadingAddressName)
        when (val result = getAddressNameUseCase(point.latitude, point.longitude)) {
            is Result.Error -> {}
            is Result.Success -> {
                _actionState.emit(MapActionState.AddressNameLoaded(result.data.name))
                _uiState.update { it.copy(selectedAddressName = result.data.name) }
            }
        }
    }

    fun getTariffs(addressId: Int, from: LatLng, to: LatLng? = null) = viewModelScope.launch {
        val coords = mutableListOf<Pair<Double, Double>>()
        coords.add(Pair(from.latitude, from.longitude))
        to?.let { coords.add(Pair(it.latitude, it.longitude)) }

        when (val result = getTariffsUseCase(listOf(), coords = coords, addressId)) {
            is Result.Error -> {}
            is Result.Success -> _uiState.update { it.copy(tariffs = result.data) }
        }
    }

    fun setSelectedAddressName(name: String?) {
        _uiState.update { it.copy(selectedAddressName = name) }
    }

    fun setSelectedAddressId(id: Int?) {
        _uiState.update { it.copy(selectedAddressId = id) }
    }

}