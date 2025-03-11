package uz.yalla.client.core.common.sheet.search_address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.feature.order.domain.usecase.FindAllMapPlacesUseCase
import uz.yalla.client.feature.map.domain.model.response.PolygonRemoteItem
import uz.yalla.client.feature.map.domain.model.response.SearchForAddressItemModel
import uz.yalla.client.feature.map.domain.usecase.GetPolygonUseCase
import uz.yalla.client.feature.map.domain.usecase.SearchAddressUseCase
import uz.yalla.client.feature.order.domain.model.response.PlaceModel
import uz.yalla.client.feature.order.domain.model.type.PlaceType

class SearchByNameBottomSheetViewModel(
    private val searchAddressUseCase: SearchAddressUseCase,
    private val findAllMapPlacesUseCase: FindAllMapPlacesUseCase,
    private val getPolygonUseCase: GetPolygonUseCase
) : ViewModel() {
    private var addresses = listOf<PolygonRemoteItem>()

    private val _uiState = MutableStateFlow(SearchByNameBottomSheetState())
    val uiState = _uiState.asStateFlow()

    fun fetchPolygons() = viewModelScope.launch {
        getPolygonUseCase().onSuccess {result-> addresses = result }
    }

    private fun searchForAddress(lat: Double, lng: Double, query: String) = viewModelScope.launch {
        searchAddressUseCase(lat, lng, query).onSuccess {result->
            setFoundAddresses(result)
        }.onFailure { setFoundAddresses(emptyList()) }
    }

    fun findAllMapAddresses() = viewModelScope.launch {
        findAllMapPlacesUseCase()
            .onSuccess { result->setMapAddresses(result) }
            .onFailure { setMapAddresses(emptyList()) }
    }

    fun setFoundAddresses(addresses: List<SearchForAddressItemModel>) {
        _uiState.update {
            it.copy(
                foundAddresses = addresses.map { address ->
                    SearchableAddress(
                        addressId = address.addressId,
                        addressName = address.addressName,
                        distance = address.distance,
                        lat = address.lat,
                        lng = address.lng,
                        name = address.name,
                        type = PlaceType.OTHER
                    )
                }
            )
        }
    }

    fun setMapAddresses(addresses: List<PlaceModel>) {
        _uiState.update {
            it.copy(
                savedAddresses = addresses.map { address ->
                    val (isInside, addressId) = isPointInsidePolygon(
                        lat = address.coords.lat,
                        lng = address.coords.lng
                    )
                    SearchableAddress(
                        addressId = if (isInside) addressId else null,
                        addressName = address.address,
                        distance = null,
                        lat = address.coords.lat,
                        lng = address.coords.lng,
                        name = address.name,
                        type = address.type
                    )
                }
            )
        }
    }

    fun setQuery(query: String) {
        _uiState.update { it.copy(query = query) }
        uiState.value.apply {
            if (currentLat != null && currentLng != null)
                searchForAddress(currentLat, currentLng, query)
        }
        if (query.isBlank()) setFoundAddresses(emptyList())
    }

    fun setDestinationQuery(query: String) {
        _uiState.update { it.copy(destinationQuery = query) }
        uiState.value.apply {
            if (currentLat != null && currentLng != null)
                searchForAddress(currentLat, currentLng, query)
        }
        if (query.isBlank()) setFoundAddresses(emptyList())
    }

    fun setCurrentLocation(lat: Double, lng: Double) = _uiState.update {
        it.copy(
            currentLat = lat,
            currentLng = lng
        )
    }

    fun isPointInsidePolygon(lat: Double, lng: Double): Pair<Boolean, Int> {
        addresses.forEach { polygonItem ->
            val vertices = polygonItem.polygons.map { Pair(it.lat, it.lng) }
            var isInside = false
            var addressId = 0
            for (i in vertices.indices) {
                val j = if (i == 0) vertices.size - 1 else i - 1
                val (lat1, lng1) = vertices[i]
                val (lat2, lng2) = vertices[j]
                val intersects = (lng1 > lng) != (lng2 > lng) &&
                        (lat < (lat2 - lat1) * (lng - lng1) / (lng2 - lng1) + lat1)
                if (intersects) {
                    isInside = !isInside
                    addressId = polygonItem.addressId
                }
            }
            if (isInside) return Pair(true, addressId)
        }
        return Pair(false, 0)
    }
}