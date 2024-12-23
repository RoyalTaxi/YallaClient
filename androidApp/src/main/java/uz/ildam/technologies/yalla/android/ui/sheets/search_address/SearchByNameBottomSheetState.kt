package uz.ildam.technologies.yalla.android.ui.sheets.search_address

import uz.ildam.technologies.yalla.feature.map.domain.model.map.SearchForAddressItemModel

data class SearchByNameBottomSheetState(
    val query: String = "",
    val currentLat: Double? = null,
    val currentLng: Double? = null,
    val foundAddresses: List<SearchForAddressItemModel> = emptyList(),
)