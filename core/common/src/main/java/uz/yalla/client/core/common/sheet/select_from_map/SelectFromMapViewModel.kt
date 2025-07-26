package uz.yalla.client.core.common.sheet.select_from_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.map.domain.model.response.PolygonRemoteItem
import uz.yalla.client.feature.map.domain.usecase.GetAddressNameUseCase
import uz.yalla.client.feature.map.domain.usecase.GetPolygonUseCase
import kotlin.time.Duration.Companion.seconds

class SelectFromMapViewModel(
    private val getPolygonUseCase: GetPolygonUseCase,
    private val getAddressNameUseCase: GetAddressNameUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SelectFromMapViewState())
    val uiState: StateFlow<SelectFromMapViewState> = _uiState.asStateFlow()

    private var polygonPollingJob: Job? = null

    private val polygonVerticesCache = mutableMapOf<Int, List<Pair<Double, Double>>>()

    init {
        startPolygonPolling()
    }

    private fun startPolygonPolling() {
        polygonPollingJob?.cancel()
        polygonPollingJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive && uiState.value.polygon.isEmpty()) {
                fetchPolygon()
                delay(5.seconds)
            }
        }
    }

    private suspend fun fetchPolygon() {
        getPolygonUseCase().onSuccess { result ->
            _uiState.update { it.copy(polygon = result) }
            if (result.isNotEmpty()) {
                result.forEach { polygonItem ->
                    polygonVerticesCache[polygonItem.addressId] = polygonItem.polygons.map {
                        Pair(it.lat, it.lng)
                    }
                }
                polygonPollingJob?.cancel()
            }
        }
    }

    fun getAddressName(point: MapPoint) = viewModelScope.launch {
        getAddressNameUseCase(point.lat, point.lng).onSuccess { data ->
            val location = Location(
                name = data.displayName,
                point = point,
                addressId = data.id
            )
            processSelectedLocation(location)
        }
    }

    private suspend fun processSelectedLocation(selectedLocation: Location) {
        val point = selectedLocation.point ?: return

        val (isInsidePolygon, polygonAddressId) = withContext(Dispatchers.Default) {
            isPointInsidePolygon(point)
        }

        val updatedLocation = if (isInsidePolygon) {
            selectedLocation.copy(addressId = polygonAddressId)
        } else {
            selectedLocation
        }

        _uiState.update { it.copy(location = updatedLocation) }
    }

    private fun isPointInsidePolygon(point: MapPoint): Pair<Boolean, Int?> {
        return uiState.value.polygon
            .asSequence()
            .map { polygonItem -> isPointInPolygon(point, polygonItem) }
            .firstOrNull { it.first }
            ?: Pair(false, null)
    }

    private fun isPointInPolygon(
        point: MapPoint,
        polygonItem: PolygonRemoteItem
    ): Pair<Boolean, Int?> {
        // Get cached vertices or compute them if not available
        val vertices = polygonVerticesCache[polygonItem.addressId] ?: polygonItem.polygons.map {
            Pair(
                it.lat,
                it.lng
            )
        }.also {
            polygonVerticesCache[polygonItem.addressId] = it
        }

        if (vertices.isEmpty()) return Pair(false, null)

        var isInside = false
        val pointLng = point.lng
        val pointLat = point.lat

        var i = 0
        var j = vertices.lastIndex
        while (i < vertices.size) {
            val (lat1, lng1) = vertices[i]
            val (lat2, lng2) = vertices[j]

            val hasVerticalCrossing = (lng1 > pointLng) != (lng2 > pointLng)
            if (hasVerticalCrossing) {
                if (lng2 - lng1 != 0.0) {
                    val intersectionLatitude =
                        (lat2 - lat1) * (pointLng - lng1) / (lng2 - lng1) + lat1
                    if (pointLat < intersectionLatitude) {
                        isInside = !isInside
                    }
                }
            }
            j = i++
        }

        return if (isInside) {
            Pair(true, if (polygonItem.addressId == 0) null else polygonItem.addressId)
        } else {
            Pair(false, null)
        }
    }


    override fun onCleared() {
        super.onCleared()
        polygonPollingJob?.cancel()
        polygonVerticesCache.clear()
    }

    fun changeStateToNotFound() {
        _uiState.update { it.copy(location = null) }
    }
}