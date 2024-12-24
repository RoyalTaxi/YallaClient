package uz.ildam.technologies.yalla.android.ui.sheets.search_address

sealed class SearchableAddress {
    data class MapAddress(
        val addressId: Int?,
        val addressName: String,
        val distance: Double?,
        val lat: Double,
        val lng: Double,
        val name: String
    ) : SearchableAddress()

    data class SearchResultAddress(
        val addressId: Int,
        val addressName: String,
        val distance: Double?,
        val lat: Double,
        val lng: Double,
        val name: String
    ) : SearchableAddress()
}
