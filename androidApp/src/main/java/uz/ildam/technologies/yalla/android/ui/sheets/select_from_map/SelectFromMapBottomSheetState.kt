package uz.ildam.technologies.yalla.android.ui.sheets.select_from_map

data class SelectFromMapBottomSheetState(
    val timeout: Int? = null,
    val name: String? = null,
    val latLng: MapPoint? = null,
    val addressId: Int? = null
)