package uz.ildam.technologies.yalla.android.ui.sheets.search_address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressModel
import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressType
import uz.ildam.technologies.yalla.feature.addresses.domain.usecase.FindAllMapAddressesUseCase
import uz.ildam.technologies.yalla.feature.map.domain.model.map.PolygonRemoteItem
import uz.ildam.technologies.yalla.feature.map.domain.model.map.SearchForAddressItemModel
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.GetPolygonUseCase
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.SearchAddressUseCase

class SearchByNameBottomSheetViewModel(
    private val searchAddressUseCase: SearchAddressUseCase,
    private val findAllMapAddressesUseCase: FindAllMapAddressesUseCase,
    private val getPolygonUseCase: GetPolygonUseCase
) : ViewModel() {
    private var addresses = listOf<PolygonRemoteItem>()

    private val _uiState = MutableStateFlow(SearchByNameBottomSheetState())
    val uiState = _uiState.asStateFlow()

    fun fetchPolygons() = viewModelScope.launch {
        getPolygonUseCase().onSuccess { addresses = it }
    }

    private fun searchForAddress(lat: Double, lng: Double, query: String) = viewModelScope.launch {
        searchAddressUseCase(lat, lng, query).onSuccess {
            setFoundAddresses(it)
        }.onFailure { setFoundAddresses(emptyList()) }
    }

    fun findAllMapAddresses() = viewModelScope.launch {
        findAllMapAddressesUseCase()
            .onSuccess { setMapAddresses(it) }
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
                        type = AddressType.OTHER
                    )
                }
            )
        }
    }

    fun setMapAddresses(addresses: List<AddressModel>) {
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