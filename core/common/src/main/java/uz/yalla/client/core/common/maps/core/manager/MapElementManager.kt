package uz.yalla.client.core.common.maps.core.manager

import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus

abstract class MapElementManager(
    protected val iconManager: MapIconManager
) {
    abstract fun setMap(map: Any)

    abstract fun clearMap()

    abstract fun clearAllElements()

    abstract fun updateRouteOnMap(
        route: List<MapPoint>,
        locations: List<MapPoint>,
        mapPaddingPx: Int,
        leftPaddingPx: Int,
        topPaddingPx: Int,
        rightPaddingPx: Int,
        bottomPaddingPx: Int,
        isDarkTheme: Boolean,
        orderStatus: OrderStatus? = null,
        animate: Boolean = true
    )

    abstract fun updateMarkersOnMap(
        locations: List<MapPoint>,
        carArrivesInMinutes: Int?,
        orderEndsInMinutes: Int?,
        orderStatus: OrderStatus?,
        hasOrder: Boolean
    )

    abstract fun updateDriverOnMap(driver: Executor?)

    abstract fun updateDriversOnMap(
        drivers: List<Executor>,
        hasOrder: Boolean
    )

    abstract fun drawDriver(driver: Executor)

    abstract fun drawRoute(
        route: List<MapPoint>,
        paddingPx: Int,
        leftPaddingPx: Int,
        topPaddingPx: Int,
        rightPaddingPx: Int,
        bottomPaddingPx: Int,
        isDarkTheme: Boolean,
        orderStatus: OrderStatus?,
        animate: Boolean = true,
        locations: List<MapPoint> = emptyList()
    )

    abstract fun drawAllMarkers(
        locations: List<MapPoint>,
        orderStatus: OrderStatus?,
        hasDestination: Boolean
    )

    abstract fun drawDashedConnections(
        locations: List<MapPoint>,
        route: List<MapPoint>
    )

    abstract fun findClosestPointOnRoute(
        location: MapPoint,
        route: List<MapPoint>
    ): MapPoint?

    open fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {}

    open fun moveTo(point: MapPoint, zoom: Float? = null) {}

    open fun animateTo(point: MapPoint) {}

    open fun setGesturesEnabled(enabled: Boolean) {}

    open fun setOnCameraIdle(listener: (MapPoint) -> Unit) {}

    open fun setOnCameraMoveStarted(listener: (isByUser: Boolean) -> Unit) {}

    open fun getCameraTarget(): MapPoint? = null

    open fun zoomOut() {}
}
