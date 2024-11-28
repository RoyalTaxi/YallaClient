package uz.ildam.technologies.yalla.android.ui.sheets.select_from_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.map.domain.model.map.PolygonRemoteItem
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
        when (val result = getPolygonUseCase()) {
            is Result.Success -> addresses = result.data
            is Result.Error -> changeStateToNotFound()
        }
    }

    fun getAddressDetails(latLng: LatLng) = viewModelScope.launch {
        if (addresses.isEmpty()) fetchPolygons()
        else addresses.firstOrNull {
            isPointInsidePolygon(
                point = latLng,
                vertices = it.polygons.map { polygon ->
                    Pair(polygon.lat, polygon.lng)
                }
            )
        }?.let { address ->
            updateSelectedLocation(addressId = address.addressId, latLng = latLng)
        } ?: run {
            updateSelectedLocation(addressId = null)
        }
        fetchAddressName(latLng)
    }


    private fun fetchAddressName(point: LatLng) = viewModelScope.launch {
        when (val result = getAddressNameUseCase(point.latitude, point.longitude)) {
            is Result.Success -> updateSelectedLocation(name = result.data.name)
            is Result.Error -> changeStateToNotFound()
        }
    }

    fun changeStateToNotFound() {
        _uiState.update {
            it.copy(
                timeout = null,
                name = null,
                latLng = null
            )
        }
    }

    private fun updateSelectedLocation(
        name: String? = _uiState.value.name,
        latLng: LatLng? = _uiState.value.latLng,
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
}