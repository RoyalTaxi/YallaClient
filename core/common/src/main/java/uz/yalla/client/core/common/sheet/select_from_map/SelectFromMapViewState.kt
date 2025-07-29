package uz.yalla.client.core.common.sheet.select_from_map

import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.feature.map.domain.model.response.PolygonRemoteItem

data class SelectFromMapViewState(
    val polygon: List<PolygonRemoteItem> = emptyList(),
    val location: Location? = null
)