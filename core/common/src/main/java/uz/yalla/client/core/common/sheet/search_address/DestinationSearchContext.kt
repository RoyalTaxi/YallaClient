package uz.yalla.client.core.common.sheet.search_address

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.milliseconds

interface DestinationSearchContext {
    val destinationQuery: MutableStateFlow<String>

    val baseViewModel: BaseAddressSearchViewModel

    @OptIn(FlowPreview::class)
    fun setupDestinationQueryFlow() {
        destinationQuery
            .debounce(500.milliseconds)
            .distinctUntilChanged()
            .onEach { query ->
                val lat = baseViewModel.uiState.value.currentLat
                val lng = baseViewModel.uiState.value.currentLng
                if (query.isNotBlank() && lat != null && lng != null) {
                    baseViewModel.searchForAddress(lat, lng, query)
                } else {
                    baseViewModel.updateState { it.copy(loading = false) }
                }
            }
            .launchIn(baseViewModel.viewModelScope)
    }

    fun setDestinationQuery(query: String) {
        baseViewModel.updateState { it.copy(destinationQuery = query, loading = true) }
        destinationQuery.value = query

        if (query.isBlank()) {
            baseViewModel.setFoundAddresses(emptyList())
            if (!baseViewModel.hasLoadedSecondaryAddresses) baseViewModel.getSecondaryAddresses()
            else baseViewModel.updateState { it.copy(loading = false) }
        }
    }

    fun setInitialDestinationQuery(query: String) {
        baseViewModel.updateState { it.copy(destinationQuery = query) }
    }

    fun resetDestinationQuery() {
        destinationQuery.value = ""
        baseViewModel.updateState { it.copy(destinationQuery = "") }
    }
}