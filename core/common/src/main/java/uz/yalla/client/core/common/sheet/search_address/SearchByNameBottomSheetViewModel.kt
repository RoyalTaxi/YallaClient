package uz.yalla.client.core.common.sheet.search_address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.domain.model.SearchableAddress
import uz.yalla.client.core.domain.model.type.PlaceType
import uz.yalla.client.feature.map.domain.model.response.PolygonRemoteItem
import uz.yalla.client.feature.map.domain.model.response.SearchForAddressItemModel
import uz.yalla.client.feature.map.domain.usecase.GetPolygonUseCase
import uz.yalla.client.feature.map.domain.usecase.GetSecondaryAddressedUseCase
import uz.yalla.client.feature.map.domain.usecase.SearchAddressUseCase

class SearchByNameBottomSheetViewModel(
    private val searchAddressUseCase: SearchAddressUseCase,
    private val getPolygonUseCase: GetPolygonUseCase,
    private val getSecondaryAddressedUseCase: GetSecondaryAddressedUseCase
) : ViewModel() {
    private var addresses = listOf<PolygonRemoteItem>()
    private var hasLoadedSecondaryAddresses = false

    private val _uiState = MutableStateFlow(SearchByNameBottomSheetState())
    val uiState = _uiState.asStateFlow()

    fun fetchPolygons() = viewModelScope.launch(Dispatchers.IO) {
        getPolygonUseCase().onSuccess { result -> addresses = result }
    }

    private fun searchForAddress(lat: Double, lng: Double, query: String) =
        viewModelScope.launch(Dispatchers.IO) {
            searchAddressUseCase(lat, lng, query).onSuccess { result ->
                setFoundAddresses(result)
            }.onFailure { setFoundAddresses(emptyList()) }
        }

    private fun setFoundAddresses(addresses: List<SearchForAddressItemModel>) {
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

    fun getSecondaryAddresses() {
        val lat = uiState.value.currentLat ?: return
        val lng = uiState.value.currentLng ?: return

        if (uiState.value.recommendedAddresses.isNotEmpty()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            getSecondaryAddressedUseCase(lat, lng).onSuccess { data ->
                _uiState.update {
                    it.copy(recommendedAddresses = data)
                }
                hasLoadedSecondaryAddresses = true
            }
        }
    }

    fun setQuery(query: String) {
        val currentQuery = uiState.value.query
        val trimmedQuery = query.trim()

        _uiState.update { it.copy(query = query) }

        if (trimmedQuery.isBlank()) {
            setFoundAddresses(emptyList())

            if (!hasLoadedSecondaryAddresses) {
                getSecondaryAddresses()
            }
            return
        }

        if (currentQuery == query) return

        uiState.value.apply {
            if (currentLat != null && currentLng != null) {
                searchForAddress(currentLat, currentLng, query)
            }
        }
    }

    fun setDestinationQuery(query: String) {
        val currentDestQuery = uiState.value.destinationQuery
        val trimmedQuery = query.trim()

        _uiState.update { it.copy(destinationQuery = query) }

        if (trimmedQuery.isBlank()) {
            setFoundAddresses(emptyList())

            if (!hasLoadedSecondaryAddresses) {
                getSecondaryAddresses()
            }
            return
        }

        if (currentDestQuery == query) return

        uiState.value.apply {
            if (currentLat != null && currentLng != null) {
                searchForAddress(currentLat, currentLng, query)
            }
        }
    }

    fun setCurrentLocation(lat: Double, lng: Double) {
        val currentLat = uiState.value.currentLat
        val currentLng = uiState.value.currentLng

        if (currentLat != lat || currentLng != lng) {
            _uiState.update {
                it.copy(
                    currentLat = lat,
                    currentLng = lng
                )
            }

            hasLoadedSecondaryAddresses = false
        }
    }

    fun resetSearchState() {
        _uiState.update {
            it.copy(
                query = "",
                foundAddresses = emptyList()
            )
        }
    }
}