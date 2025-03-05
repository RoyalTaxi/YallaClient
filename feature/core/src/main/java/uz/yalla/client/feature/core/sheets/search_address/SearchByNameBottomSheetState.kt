package uz.yalla.client.feature.core.sheets.search_address

data class SearchByNameBottomSheetState(
    val query: String = "",
    val destinationQuery: String = "",
    val currentLat: Double? = null,
    val currentLng: Double? = null,
    val foundAddresses: List<SearchableAddress> = emptyList(),
    val savedAddresses: List<SearchableAddress> = emptyList()
)