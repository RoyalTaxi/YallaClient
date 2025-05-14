package uz.yalla.client.core.common.sheet.search_address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.yalla.client.core.domain.model.SearchableAddress
import uz.yalla.client.core.domain.model.type.PlaceType
import uz.yalla.client.feature.map.domain.model.response.PolygonRemoteItem
import uz.yalla.client.feature.map.domain.model.response.SearchForAddressItemModel
import uz.yalla.client.feature.map.domain.usecase.GetPolygonUseCase
import uz.yalla.client.feature.map.domain.usecase.GetSecondaryAddressedUseCase
import uz.yalla.client.feature.map.domain.usecase.SearchAddressUseCase
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
open class BaseAddressSearchViewModel(
    private val searchAddressUseCase: SearchAddressUseCase,
    private val getPolygonUseCase: GetPolygonUseCase,
    private val getSecondaryAddressedUseCase: GetSecondaryAddressedUseCase
) : ViewModel() {

    private var addresses = listOf<PolygonRemoteItem>()
    var hasLoadedSecondaryAddresses = false

    private val _uiState = MutableStateFlow(AddressSearchState())
    val uiState: StateFlow<AddressSearchState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        _searchQuery
            .debounce(500.milliseconds)
            .distinctUntilChanged()
            .onEach { query ->
                val lat = uiState.value.currentLat
                val lng = uiState.value.currentLng
                if (query.isNotBlank() && lat != null && lng != null) {
                    searchForAddress(lat, lng, query)
                } else {
                    _uiState.update { it.copy(loading = false) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun fetchPolygons() = viewModelScope.launch(Dispatchers.IO) {
        getPolygonUseCase().onSuccess { result -> addresses = result }
    }

    fun searchForAddress(lat: Double, lng: Double, query: String) =
        viewModelScope.launch(Dispatchers.IO) {
            searchAddressUseCase(lat, lng, query).onSuccess { result ->
                setFoundAddresses(result)
            }.onFailure {
                setFoundAddresses(emptyList())
            }
        }

    fun setFoundAddresses(addresses: List<SearchForAddressItemModel>) {
        _uiState.update {
            it.copy(
                loading = false,
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

    open fun getSecondaryAddresses() {
        val lat = uiState.value.currentLat ?: return
        val lng = uiState.value.currentLng ?: return

        if (uiState.value.recommendedAddresses.isNotEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    loading = true,
                    foundAddresses = emptyList()
                )
            }

            getSecondaryAddressedUseCase(lat, lng).onSuccess { data ->
                _uiState.update {
                    it.copy(
                        recommendedAddresses = data,
                        loading = false
                    )
                }
                hasLoadedSecondaryAddresses = true
            }.onFailure {
                _uiState.update { it.copy(loading = false) }
            }
        }
    }

    open fun setQuery(query: String) {
        _uiState.update { it.copy(query = query, loading = true) }
        _searchQuery.value = query

        if (query.isBlank()) {
            setFoundAddresses(emptyList())
            if (!hasLoadedSecondaryAddresses) getSecondaryAddresses()
            else _uiState.update { it.copy(loading = false) }
        }
    }

    fun setInitialQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun setCurrentLocation(lat: Double, lng: Double) {
        val currentLat = uiState.value.currentLat
        val currentLng = uiState.value.currentLng

        if (currentLat != lat || currentLng != lng) {
            _uiState.update {
                it.copy(currentLat = lat, currentLng = lng)
            }
            hasLoadedSecondaryAddresses = false
        }
    }

    open fun resetSearchState() {
        _searchQuery.value = ""
        _uiState.update {
            it.copy(
                query = "",
                recommendedAddresses = emptyList(),
                foundAddresses = emptyList(),
                loading = false
            )
        }
    }

    fun updateState(update: (AddressSearchState) -> AddressSearchState) {
        _uiState.update(update)
    }
}