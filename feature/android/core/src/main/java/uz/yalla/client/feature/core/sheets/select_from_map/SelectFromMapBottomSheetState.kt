package uz.yalla.client.feature.core.sheets.select_from_map

import uz.ildam.technologies.yalla.core.domain.model.MapPoint

data class SelectFromMapBottomSheetState(
    val timeout: Int? = null,
    val name: String? = null,
    val latLng: MapPoint? = null,
    val addressId: Int? = null
)