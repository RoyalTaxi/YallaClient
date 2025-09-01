package uz.yalla.client.core.common.maps.core.manager

import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus

abstract class MapElementManager(
    protected val iconManager: MapIconManager
) {
    /**
     * Set the map instance
     */
    abstract fun setMap(map: Any)

    /**
     * Clear the map
     */
    abstract fun clearMap()

    /**
     * Clear all elements from the map
     */
    abstract fun clearAllElements()

    /**
     * Update the route on the map
     */
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

    /**
     * Update markers on the map
     */
    abstract fun updateMarkersOnMap(
        locations: List<MapPoint>,
        carArrivesInMinutes: Int?,
        orderEndsInMinutes: Int?,
        orderStatus: OrderStatus?,
        hasOrder: Boolean
    )

    /**
     * Update a driver on the map
     */
    abstract fun updateDriverOnMap(driver: Executor?)

    /**
     * Update multiple drivers on the map
     */
    abstract fun updateDriversOnMap(
        drivers: List<Executor>,
        hasOrder: Boolean
    )

    /**
     * Draw a driver on the map
     */
    abstract fun drawDriver(driver: Executor)

    /**
     * Draw a route on the map
     */
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

    /**
     * Draw all markers on the map
     */
    abstract fun drawAllMarkers(
        locations: List<MapPoint>,
        orderStatus: OrderStatus?,
        hasDestination: Boolean
    )

    /**
     * Draw dashed connections between points
     */
    abstract fun drawDashedConnections(
        locations: List<MapPoint>,
        route: List<MapPoint>
    )

    /**
     * Find the closest point on a route
     */
    abstract fun findClosestPointOnRoute(
        location: MapPoint,
        route: List<MapPoint>
    ): MapPoint?

    // --- Cross‑platform map operations (default no‑ops) ---
    open fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {}

    open fun moveTo(point: MapPoint, zoom: Float? = null) {}

    open fun animateTo(point: MapPoint) {}

    open fun setGesturesEnabled(enabled: Boolean) {}

    open fun setOnCameraIdle(listener: (MapPoint) -> Unit) {}

    open fun setOnCameraMoveStarted(listener: (isByUser: Boolean) -> Unit) {}

    open fun getCameraTarget(): MapPoint? = null

    open fun zoomOut() {}
}
