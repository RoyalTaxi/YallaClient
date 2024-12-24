package uz.ildam.technologies.yalla.android.ui.sheets.search_address

data class SearchByNameBottomSheetState(
    val query: String = "",
    val currentLat: Double? = null,
    val currentLng: Double? = null,
    val foundAddresses: List<SearchableAddress> = emptyList(),
    val savedAddresses: List<SearchableAddress> = emptyList()
)