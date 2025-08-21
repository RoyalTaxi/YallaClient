package uz.yalla.client.core.common.maps.model

import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

data class MapState(
    val isMapReady: Boolean = false,
    val savedCameraPosition: MapPoint? = null,
    val mapPaddingPx: Int = 0,
    val topPaddingPx: Int = 0,
    val bottomPaddingPx: Int = 0,
    val googleMarkPaddingPx: Int = 0,
    val route: List<MapPoint> = emptyList(),
    val locations: List<MapPoint> = emptyList(),
    val carArrivesInMinutes: Int? = null,
    val orderEndsInMinutes: Int? = null,
    val order: ShowOrderModel? = null,
    val driver: Executor? = null,
    val drivers: List<Executor> = emptyList()
)
