package uz.yalla.client.core.common.sheet.search_address

import uz.yalla.client.core.domain.model.SearchableAddress

data class AddressSearchState(
    val query: String = "",
    val destinationQuery: String = "",
    val foundAddresses: List<SearchableAddress> = emptyList(),
    val recommendedAddresses: List<SearchableAddress> = emptyList(),
    val loading: Boolean = false,
    val currentLat: Double? = null,
    val currentLng: Double? = null
)