package uz.ildam.technologies.yalla.android.ui.sheets.search_address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.map.domain.model.map.SearchForAddressItemModel
import uz.ildam.technologies.yalla.feature.map.domain.usecase.map.SearchAddressUseCase

class SearchByNameBottomSheetViewModel(
    private val searchAddressUseCase: SearchAddressUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchByNameBottomSheetState())
    val uiState = _uiState.asStateFlow()

    private fun searchForAddress(lat: Double, lng: Double, query: String) = viewModelScope.launch {
        when (val result = searchAddressUseCase(lat, lng, query)) {
            is Result.Error -> setFoundAddresses(emptyList())
            is Result.Success -> setFoundAddresses(result.data)
        }
    }

    fun setFoundAddresses(addresses: List<SearchForAddressItemModel>) {
        _uiState.update { it.copy(foundAddresses = addresses) }
    }

    fun setQuery(query: String) {
        _uiState.update { it.copy(query = query) }
        uiState.value.apply {
            if (currentLat != null && currentLng != null)
                searchForAddress(currentLat, currentLng, query)
        }
    }

    fun setCurrentLocation(lat: Double, lng: Double) = _uiState.update {
        it.copy(
            currentLat = lat,
            currentLng = lng
        )
    }
}