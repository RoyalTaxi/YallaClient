package uz.yalla.client.core.common.maps.google.manager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import uz.yalla.client.core.common.maps.core.manager.MapElementManager
import uz.yalla.client.core.common.maps.google.util.GMapConstants
import uz.yalla.client.core.common.R
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus

class GMapElementManager(
    iconManager: GMapIconManager
) : MapElementManager(iconManager) {
    private var map: GoogleMap? = null
    private var polyline: Polyline? = null
    private val dashedPolylines = mutableListOf<Polyline>()
    private val markers = mutableListOf<Marker>()
    private val driverMarkers = mutableListOf<Marker>()
    private val driversMarkers = mutableListOf<Marker>()

    private var lastDriverPosition: Pair<Double, Double>? = null
    private var lastDriverHeading: Float? = null

    private val driverMarkersById = mutableMapOf<Int, Marker>()
    private val lastDriverPosById = mutableMapOf<Int, Pair<Double, Double>>()
    private val lastDriverHeadingById = mutableMapOf<Int, Float>()

    private val lastDriversPositions = mutableMapOf<Int, Pair<Double, Double>>()
    private val lastDriversHeadings = mutableMapOf<Int, Float>()

    override fun setMap(map: Any) {
        if (map is GoogleMap) {
            this.map = map
        }
    }

    override fun clearMap() {
        map = null
    }

    override fun clearAllElements() {
        markers.forEach { it.remove() }
        markers.clear()

        driverMarkers.forEach { it.remove() }
        driverMarkers.clear()

        driversMarkers.forEach { it.remove() }
        driversMarkers.clear()

        driverMarkersById.values.forEach { it.remove() }
        driverMarkersById.clear()

        polyline?.remove()
        polyline = null

        dashedPolylines.forEach { it.remove() }
        dashedPolylines.clear()

        lastDriverPosition = null
        lastDriverHeading = null
        lastDriverPosById.clear()
        lastDriverHeadingById.clear()
        lastDriversPositions.clear()
        lastDriversHeadings.clear()
    }

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
        polyline?.remove()
        polyline = null

        dashedPolylines.forEach { it.remove() }
        dashedPolylines.clear()

        if (route.isNotEmpty()) {
            drawRoute(route, mapPaddingPx, leftPaddingPx, topPaddingPx, rightPaddingPx, bottomPaddingPx, isDarkTheme, orderStatus, animate, locations)
        }

        if (locations.isNotEmpty()) {
            drawDashedConnections(locations, route)
        }
    }

    override fun updateMarkersOnMap(
        locations: List<MapPoint>,
        carArrivesInMinutes: Int?,
        orderEndsInMinutes: Int?,
        orderStatus: OrderStatus?,
        hasOrder: Boolean
    ) {
        // Clear existing location markers
        markers.forEach { it.remove() }
        markers.clear()

        // Create info marker icons if needed
        iconManager.createMarkerIcons(carArrivesInMinutes, orderEndsInMinutes, hasOrder)

        // Draw all location markers
        if (locations.isNotEmpty()) {
            drawAllMarkers(
                locations = locations,
                orderStatus = orderStatus,
                hasDestination = locations.size > 1
            )
        }
    }

    /**
     * Updates the driver marker on the map
     */
    override fun updateDriverOnMap(driver: Executor?) {
        if (driver == null) {
            driverMarkers.forEach { it.remove() }
            driverMarkers.clear()

            lastDriverPosition = null
            lastDriverHeading = null
            return
        }

        val currentPosition = Pair(driver.lat, driver.lng)
        val currentHeading = driver.heading.toFloat()

        if (lastDriverPosition != currentPosition || lastDriverHeading != currentHeading) {
            if (driverMarkers.isNotEmpty()) {
                driverMarkers.first().apply {
                    position = LatLng(driver.lat, driver.lng)
                    rotation = currentHeading
                }
            } else {
                drawDriver(driver)
            }

            lastDriverPosition = currentPosition
            lastDriverHeading = currentHeading
        }
    }

    override fun updateDriversOnMap(drivers: List<Executor>, hasOrder: Boolean) {
        if (drivers.isEmpty() || hasOrder) {
            driverMarkersById.values.forEach { it.remove() }
            driverMarkersById.clear()
            lastDriverPosById.clear()
            lastDriverHeadingById.clear()

            driversMarkers.forEach { it.remove() }
            driversMarkers.clear()
            lastDriversPositions.clear()
            lastDriversHeadings.clear()
            return
        }

        val aliveIds = drivers.map { it.id }.toSet()

        val idsToRemove = driverMarkersById.keys.filter { it !in aliveIds }
        idsToRemove.forEach { id ->
            driverMarkersById.remove(id)?.remove()
            lastDriverPosById.remove(id)
            lastDriverHeadingById.remove(id)
        }

        drivers.forEach { driver ->
            val currentPosition = Pair(driver.lat, driver.lng)
            val currentHeading = driver.heading.toFloat()

            val positionChanged = lastDriverPosById[driver.id] != currentPosition
            val headingChanged = lastDriverHeadingById[driver.id] != currentHeading

            val existingMarker = driverMarkersById[driver.id]

            if (existingMarker == null) {
                map?.let { googleMap ->
                    val markerOptions = MarkerOptions()
                        .position(LatLng(driver.lat, driver.lng))
                        .flat(true)
                        .rotation(currentHeading)
                        .anchor(0.5f, 0.5f)
                        .zIndex(1f)
                        .icon(iconManager.requireDriverIcon() as com.google.android.gms.maps.model.BitmapDescriptor)

                    googleMap.addMarker(markerOptions)?.let {
                        it.tag = driver.id
                        driverMarkersById[driver.id] = it
                        lastDriverPosById[driver.id] = currentPosition
                        lastDriverHeadingById[driver.id] = currentHeading
                    }
                }
            } else if (positionChanged || headingChanged) {
                existingMarker.apply {
                    if (positionChanged) position = LatLng(driver.lat, driver.lng)
                    if (headingChanged) rotation = currentHeading
                }

                lastDriverPosById[driver.id] = currentPosition
                lastDriverHeadingById[driver.id] = currentHeading
            }
        }
    }

    override fun drawDriver(driver: Executor) {
        map?.let { googleMap ->
            val markerOptions = MarkerOptions()
                .position(LatLng(driver.lat, driver.lng))
                .flat(true)
                .rotation(driver.heading.toFloat())
                .anchor(0.5f, 0.5f)
                .zIndex(1f)
                .icon(iconManager.requireDriverIcon() as com.google.android.gms.maps.model.BitmapDescriptor)

            googleMap.addMarker(markerOptions)?.let {
                driverMarkers.add(it)
            }
        }
    }

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
        polyline?.remove()
        polyline = null
        if (route.size < 2) return

        map?.let { googleMap ->
            val latLngPoints = route.map { LatLng(it.lat, it.lng) }
            val boundsBuilder = LatLngBounds.Builder()

            route.forEach { point ->
                boundsBuilder.include(LatLng(point.lat, point.lng))
            }

            locations.forEach { point ->
                boundsBuilder.include(LatLng(point.lat, point.lng))
            }

            val bounds = boundsBuilder.build()

            val polylineColor = iconManager.color(R.color.map_polyline_google)

            polyline = googleMap.addPolyline(
                PolylineOptions()
                    .addAll(latLngPoints)
                    .color(polylineColor)
                    .width(GMapConstants.POLYLINE_WIDTH)
            )

            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, paddingPx)
            if (orderStatus !in OrderStatus.nonInteractive)
                if (animate) googleMap.animateCamera(cameraUpdate)
                else googleMap.moveCamera(cameraUpdate)
        }
    }

    override fun drawAllMarkers(
        locations: List<MapPoint>,
        orderStatus: OrderStatus?,
        hasDestination: Boolean
    ) {
        if (locations.isEmpty()) return

        map?.let { googleMap ->
            locations.forEachIndexed { index, point ->
                val markerOptions =
                    MarkerOptions().position(LatLng(point.lat, point.lng)).zIndex(2f)

                when (index) {
                    0 -> {
                        if (orderStatus != null || hasDestination) {
                            val icon = (iconManager.originInfoIcon
                                ?: iconManager.requireOriginIcon()) as com.google.android.gms.maps.model.BitmapDescriptor
                            markerOptions.icon(icon)
                            googleMap.addMarker(markerOptions)?.let { markers.add(it) }
                        }
                    }

                    locations.lastIndex -> {
                        val icon = (iconManager.destinationInfoIcon
                            ?: iconManager.requireDestinationIcon()) as com.google.android.gms.maps.model.BitmapDescriptor
                        markerOptions.icon(icon)
                        googleMap.addMarker(markerOptions)?.let { markers.add(it) }
                    }

                    else -> {
                        val icon = iconManager.requireMiddleIcon() as com.google.android.gms.maps.model.BitmapDescriptor
                        markerOptions.icon(icon)
                        googleMap.addMarker(markerOptions)?.let { markers.add(it) }
                    }
                }
            }
        }
    }

    override fun drawDashedConnections(
        locations: List<MapPoint>,
        route: List<MapPoint>
    ) {
        map?.let { googleMap ->
            locations.forEachIndexed { index, location ->
                val target: MapPoint? = when (index) {
                    0 -> route.firstOrNull()
                    locations.lastIndex -> route.lastOrNull()
                    else -> findClosestPointOnRoute(location, route)
                }

                if (target != null && target != location) {
                    val dashedPolyline = googleMap.addPolyline(
                        PolylineOptions()
                            .add(LatLng(location.lat, location.lng))
                            .add(LatLng(target.lat, target.lng))
                            .color(iconManager.color(R.color.map_dashed_polyline_google))
                            .width(GMapConstants.DASHED_POLYLINE_WIDTH)
                            .pattern(listOf(Dash(16f), Gap(16f)))
                    )
                    dashedPolylines.add(dashedPolyline)
                }
            }
        }
    }

    override fun findClosestPointOnRoute(location: MapPoint, route: List<MapPoint>): MapPoint? {
        if (route.isEmpty()) return null

        return route.minByOrNull { routePoint ->
            val latDiff = location.lat - routePoint.lat
            val lngDiff = location.lng - routePoint.lng
            latDiff * latDiff + lngDiff * lngDiff
        }
    }

    // --- Cross‑platform map operations ---
    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        map?.setPadding(left, top, right, bottom)
    }

    override fun moveTo(point: MapPoint, zoom: Float?) {
        map?.let { googleMap ->
            val z = zoom ?: GMapConstants.DEFAULT_ZOOM
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(point.lat, point.lng), z)
            )
        }
    }

    override fun animateTo(point: MapPoint) {
        map?.animateCamera(CameraUpdateFactory.newLatLng(LatLng(point.lat, point.lng)))
    }

    override fun setGesturesEnabled(enabled: Boolean) {
        map?.uiSettings?.apply {
            isScrollGesturesEnabled = enabled
            isZoomGesturesEnabled = enabled
            isTiltGesturesEnabled = enabled
            isRotateGesturesEnabled = enabled
        }
    }

    override fun setOnCameraIdle(listener: (MapPoint) -> Unit) {
        map?.setOnCameraIdleListener {
            val target = map?.cameraPosition?.target
            if (target != null) {
                listener(MapPoint(target.latitude, target.longitude))
            }
        }
    }

    override fun setOnCameraMoveStarted(listener: (isByUser: Boolean) -> Unit) {
        map?.setOnCameraMoveStartedListener { reason ->
            val isByUser = reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE
            listener(isByUser)
        }
    }

    override fun getCameraTarget(): MapPoint? {
        val t = map?.cameraPosition?.target ?: return null
        return MapPoint(t.latitude, t.longitude)
    }

    override fun zoomOut() {
        map?.animateCamera(CameraUpdateFactory.zoomOut())
    }
}
