package uz.yalla.client.core.common.maps.core.manager

import com.google.android.gms.maps.GoogleMap
import org.maplibre.android.maps.MapLibreMap
import uz.yalla.client.core.common.maps.google.manager.GMapElementManager
import uz.yalla.client.core.common.maps.google.manager.GMapIconManager
import uz.yalla.client.core.common.maps.libre.manager.LMapElementManager
import uz.yalla.client.core.common.maps.libre.manager.LMapIconManager
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus

/**
 * Delegates element operations to the correct provider manager after setMap(map) is called.
 */
class DelegatingMapElementManager(
    googleIconManager: GMapIconManager,
    libreIconManager: LMapIconManager
) : MapElementManager(googleIconManager) { // base requires an iconManager; unused here

    private val gManager = GMapElementManager(googleIconManager)
    private val lManager = LMapElementManager(libreIconManager)
    private var active: MapElementManager? = null

    override fun setMap(map: Any) {
        active = when (map) {
            is GoogleMap -> {
                gManager.setMap(map)
                gManager
            }
            is MapLibreMap -> {
                lManager.setMap(map)
                lManager
            }
            else -> null
        }
    }

    override fun clearMap() { active = null }

    override fun clearAllElements() { active?.clearAllElements() }

    override fun updateRouteOnMap(
        route: List<MapPoint>,
        locations: List<MapPoint>,
        mapPaddingPx: Int,
        leftPaddingPx: Int,
        topPaddingPx: Int,
        rightPaddingPx: Int,
        bottomPaddingPx: Int,
        isDarkTheme: Boolean,
        orderStatus: OrderStatus?,
        animate: Boolean
    ) {
        active?.updateRouteOnMap(
            route,
            locations,
            mapPaddingPx,
            leftPaddingPx,
            topPaddingPx,
            rightPaddingPx,
            bottomPaddingPx,
            isDarkTheme,
            orderStatus,
            animate
        )
    }

    override fun updateMarkersOnMap(
        locations: List<MapPoint>,
        carArrivesInMinutes: Int?,
        orderEndsInMinutes: Int?,
        orderStatus: OrderStatus?,
        hasOrder: Boolean
    ) { active?.updateMarkersOnMap(locations, carArrivesInMinutes, orderEndsInMinutes, orderStatus, hasOrder) }

    override fun updateDriverOnMap(driver: Executor?) { active?.updateDriverOnMap(driver) }

    override fun updateDriversOnMap(drivers: List<Executor>, hasOrder: Boolean) { active?.updateDriversOnMap(drivers, hasOrder) }

    override fun drawDriver(driver: Executor) { active?.drawDriver(driver) }

    override fun drawRoute(
        route: List<MapPoint>,
        paddingPx: Int,
        leftPaddingPx: Int,
        topPaddingPx: Int,
        rightPaddingPx: Int,
        bottomPaddingPx: Int,
        isDarkTheme: Boolean,
        orderStatus: OrderStatus?,
        animate: Boolean,
        locations: List<MapPoint>
    ) {
        active?.drawRoute(
            route,
            paddingPx,
            leftPaddingPx,
            topPaddingPx,
            rightPaddingPx,
            bottomPaddingPx,
            isDarkTheme,
            orderStatus,
            animate,
            locations
        )
    }

    override fun drawAllMarkers(
        locations: List<MapPoint>,
        orderStatus: OrderStatus?,
        hasDestination: Boolean
    ) { active?.drawAllMarkers(locations, orderStatus, hasDestination) }

    override fun drawDashedConnections(locations: List<MapPoint>, route: List<MapPoint>) { active?.drawDashedConnections(locations, route) }

    override fun findClosestPointOnRoute(location: MapPoint, route: List<MapPoint>): MapPoint? = active?.findClosestPointOnRoute(location, route)

    // Cross-platform helpers
    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) { active?.setPadding(left, top, right, bottom) }
    override fun moveTo(point: MapPoint, zoom: Float?) { active?.moveTo(point, zoom) }
    override fun animateTo(point: MapPoint) { active?.animateTo(point) }
    override fun setGesturesEnabled(enabled: Boolean) { active?.setGesturesEnabled(enabled) }
    override fun setOnCameraIdle(listener: (MapPoint) -> Unit) { active?.setOnCameraIdle(listener) }
    override fun setOnCameraMoveStarted(listener: (isByUser: Boolean) -> Unit) { active?.setOnCameraMoveStarted(listener) }
    override fun getCameraTarget(): MapPoint? = active?.getCameraTarget()
    override fun zoomOut() { active?.zoomOut() }
}
