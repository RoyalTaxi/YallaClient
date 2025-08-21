package uz.yalla.client.core.common.maps.manager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import uz.yalla.client.core.common.maps.util.MapConstants
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus

class MapElementManager(
    private val iconManager: MapIconManager
) {
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

    fun setMap(googleMap: GoogleMap) {
        map = googleMap
    }

    fun clearMap() {
        map = null
    }

    fun clearAllElements() {
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

    /**
     * Updates the route displayed on the map
     */
    fun updateRouteOnMap(
        route: List<MapPoint>, 
        locations: List<MapPoint>,
        mapPaddingPx: Int,
        isDarkTheme: Boolean,
        orderStatus: OrderStatus? = null,
        animate: Boolean = true
    ) {
        // Clear existing route
        polyline?.remove()
        polyline = null

        dashedPolylines.forEach { it.remove() }
        dashedPolylines.clear()

        // Draw regular route
        if (route.isNotEmpty()) {
            drawRoute(route, mapPaddingPx, isDarkTheme, orderStatus, animate, locations)
        }

        // Draw dashed connections
        if (locations.isNotEmpty()) {
            drawDashedConnections(locations, route)
        }
    }

    /**
     * Updates the markers displayed on the map
     */
    fun updateMarkersOnMap(
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
    fun updateDriverOnMap(driver: Executor?) {
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

    fun updateDriversOnMap(drivers: List<Executor>, hasOrder: Boolean) {
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
                        .icon(iconManager.requireDriverIcon())

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

    private fun drawDriver(driver: Executor) {
        map?.let { googleMap ->
            val markerOptions = MarkerOptions()
                .position(LatLng(driver.lat, driver.lng))
                .flat(true)
                .rotation(driver.heading.toFloat())
                .anchor(0.5f, 0.5f)
                .zIndex(1f)
                .icon(iconManager.requireDriverIcon())

            googleMap.addMarker(markerOptions)?.let {
                driverMarkers.add(it)
            }
        }
    }

    private fun drawRoute(
        route: List<MapPoint>, 
        paddingPx: Int, 
        isDarkTheme: Boolean,
        orderStatus: OrderStatus?,
        animate: Boolean = true,
        locations: List<MapPoint> = emptyList()
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

            val polylineColor = if (isDarkTheme) {
                MapConstants.POLYLINE_COLOR_NIGHT
            } else {
                MapConstants.POLYLINE_COLOR_DAY
            }

            polyline = googleMap.addPolyline(
                PolylineOptions()
                    .addAll(latLngPoints)
                    .color(polylineColor)
                    .width(MapConstants.POLYLINE_WIDTH)
            )

            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, paddingPx)
            if (orderStatus !in OrderStatus.nonInteractive)
                if (animate) googleMap.animateCamera(cameraUpdate)
                else googleMap.moveCamera(cameraUpdate)
        }
    }

    private fun drawAllMarkers(
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
                            val icon = iconManager.originInfoIcon ?: iconManager.requireOriginIcon()
                            markerOptions.icon(icon)
                            googleMap.addMarker(markerOptions)?.let { markers.add(it) }
                        }
                    }

                    locations.lastIndex -> {
                        val icon = iconManager.destinationInfoIcon ?: iconManager.requireDestinationIcon()
                        markerOptions.icon(icon)
                        googleMap.addMarker(markerOptions)?.let { markers.add(it) }
                    }

                    else -> {
                        val icon = iconManager.requireMiddleIcon()
                        markerOptions.icon(icon)
                        googleMap.addMarker(markerOptions)?.let { markers.add(it) }
                    }
                }
            }
        }
    }

    private fun drawDashedConnections(
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
                            .color(MapConstants.DASHED_POLYLINE_COLOR)
                            .width(MapConstants.DASHED_POLYLINE_WIDTH)
                            .pattern(listOf(Dash(16f), Gap(16f)))
                    )
                    dashedPolylines.add(dashedPolyline)
                }
            }
        }
    }

    private fun findClosestPointOnRoute(location: MapPoint, route: List<MapPoint>): MapPoint? {
        if (route.isEmpty()) return null

        return route.minByOrNull { routePoint ->
            val latDiff = location.lat - routePoint.lat
            val lngDiff = location.lng - routePoint.lng
            latDiff * latDiff + lngDiff * lngDiff
        }
    }
}
