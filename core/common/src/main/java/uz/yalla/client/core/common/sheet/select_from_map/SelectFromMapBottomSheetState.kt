package uz.yalla.client.core.common.sheet.select_from_map

import uz.yalla.client.core.domain.model.MapPoint

data class SelectFromMapBottomSheetState(
    val timeout: Int? = null,
    val name: String? = null,
    val latLng: MapPoint? = null,
    val addressId: Int? = null
)