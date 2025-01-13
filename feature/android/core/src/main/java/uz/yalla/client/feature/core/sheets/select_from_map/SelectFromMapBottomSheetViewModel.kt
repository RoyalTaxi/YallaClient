package uz.yalla.client.feature.core.sheets.select_from_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.domain.model.MapPoint
import uz.ildam.technologies.yalla.feature.map.domain.model.response.map.PolygonRemoteItem
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.GetAddressNameUseCase
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.GetPolygonUseCase

class SelectFromMapBottomSheetViewModel(
    private val getPolygonUseCase: GetPolygonUseCase,
    private val getAddressNameUseCase: GetAddressNameUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SelectFromMapBottomSheetState())
    val uiState = _uiState.asStateFlow()

    private var addresses = listOf<PolygonRemoteItem>()

    init {
        fetchPolygons()
    }

    private fun fetchPolygons() = viewModelScope.launch {
        getPolygonUseCase()
            .onSuccess { result -> addresses = result }
            .onFailure { changeStateToNotFound() }
    }

    fun getAddressDetails(point: MapPoint) = viewModelScope.launch {
        if (addresses.isEmpty()) fetchPolygons()
        else addresses.firstOrNull {
            isPointInsidePolygon(
                point = point,
                vertices = it.polygons.map { polygon ->
                    Pair(polygon.lat, polygon.lng)
                }
            )
        }?.let { address ->
            updateSelectedLocation(addressId = address.addressId, latLng = point)
        } ?: run {
            updateSelectedLocation(addressId = null)
        }
        fetchAddressName(point)
    }


    private fun fetchAddressName(point: MapPoint) = viewModelScope.launch {
        getAddressNameUseCase(point.lat, point.lng)
            .onSuccess { result ->
                updateSelectedLocation(
                    name = result.displayName,
                    latLng = point
                )
            }
            .onFailure {
                changeStateToNotFound()
            }
    }

    fun changeStateToNotFound() {
        _uiState.update {
            it.copy(
                timeout = null,
                name = null,
                latLng = null,
                addressId = null
            )
        }
    }

    private fun updateSelectedLocation(
        name: String? = _uiState.value.name,
        latLng: MapPoint? = _uiState.value.latLng,
        addressId: Int? = _uiState.value.addressId
    ) {
        _uiState.update {
            it.copy(
                name = name,
                latLng = latLng,
                addressId = addressId
            )
        }
    }

    private fun isPointInsidePolygon(
        point: MapPoint,
        vertices: List<Pair<Double, Double>>
    ): Boolean {
        var isInside = false
        for (i in vertices.indices) {
            val j = if (i == 0) vertices.size - 1 else i - 1
            val (lat1, lng1) = vertices[i]
            val (lat2, lng2) = vertices[j]
            val intersects = (lng1 > point.lng) != (lng2 > point.lng) &&
                    (point.lat < (lat2 - lat1) * (point.lng - lng1) / (lng2 - lng1) + lat1)
            if (intersects) isInside = !isInside
        }
        return isInside
    }
}