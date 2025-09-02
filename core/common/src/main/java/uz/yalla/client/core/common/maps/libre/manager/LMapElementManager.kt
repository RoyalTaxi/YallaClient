package uz.yalla.client.core.common.maps.libre.manager

import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.Polyline
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import uz.yalla.client.core.common.maps.core.manager.MapElementManager
import uz.yalla.client.core.common.maps.libre.util.LMapConstants
import uz.yalla.client.core.common.R
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus

class LMapElementManager(
    iconManager: LMapIconManager
) : MapElementManager(iconManager) {
    private var map: MapLibreMap? = null
    private var polyline: Polyline? = null
    private val dashedPolylines = mutableListOf<Polyline>()
    private val markers = mutableListOf<Marker>()
    private val driverMarkers = mutableListOf<Marker>()
    private val driverMarkersById = mutableMapOf<Int, Marker>()

    private var lastDriverPosition: Pair<Double, Double>? = null
    private var lastDriverHeading: Float? = null
    private val lastDriverPosById = mutableMapOf<Int, Pair<Double, Double>>()
    private val lastDriverHeadingById = mutableMapOf<Int, Float>()

    // Track route identity to avoid unnecessary redraws on camera refocus
    private var lastRouteSignature: String? = null

    override fun setMap(map: Any) {
        this.map = map as? MapLibreMap
    }

    override fun clearMap() {
        map = null
    }

    override fun clearAllElements() {
        markers.forEach { it.remove() }
        markers.clear()

        driverMarkers.forEach { it.remove() }
        driverMarkers.clear()

        driverMarkersById.values.forEach { it.remove() }
        driverMarkersById.clear()
        lastDriverPosById.clear()
        lastDriverHeadingById.clear()

        polyline?.remove()
        polyline = null

        dashedPolylines.forEach { it.remove() }
        dashedPolylines.clear()

        lastDriverPosition = null
        lastDriverHeading = null
        lastRouteSignature = null
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
        val currentSignature = computeRouteSignature(route)
        val routeChanged = currentSignature != lastRouteSignature

        // Only redraw polyline and dashed connections if route actually changed
        if (routeChanged || polyline == null) {
            polyline?.remove(); polyline = null
            dashedPolylines.forEach { it.remove() }; dashedPolylines.clear()

            if (route.isNotEmpty()) {
                drawRoute(
                    route,
                    mapPaddingPx,
                    leftPaddingPx,
                    topPaddingPx,
                    rightPaddingPx,
                    bottomPaddingPx,
                    isDarkTheme,
                    orderStatus,
                    animate,
                    locations
                )
                lastRouteSignature = currentSignature
            }

            if (locations.isNotEmpty()) {
                drawDashedConnections(locations, route)
            }
        } else {
            // Route is unchanged and already drawn — only adjust camera if requested
            if (route.isNotEmpty()) {
                val boundsBuilder = LatLngBounds.Builder()
                route.forEach { boundsBuilder.include(LatLng(it.lat, it.lng)) }
                locations.forEach { boundsBuilder.include(LatLng(it.lat, it.lng)) }
                val bounds = try { boundsBuilder.build() } catch (_: Throwable) { null }
                val m = map
                if (bounds != null && m != null && orderStatus !in OrderStatus.nonInteractive) {
                    try {
                        val left = leftPaddingPx + mapPaddingPx
                        val top = topPaddingPx + mapPaddingPx
                        val right = rightPaddingPx + mapPaddingPx
                        val bottom = bottomPaddingPx + mapPaddingPx
                        val cam = m.getCameraForLatLngBounds(
                            bounds,
                            intArrayOf(left, top, right, bottom)
                        )
                        if (cam != null) {
                            if (animate) m.animateCamera(
                                org.maplibre.android.camera.CameraUpdateFactory.newCameraPosition(cam)
                            ) else m.cameraPosition = cam
                        }
                    } catch (_: Throwable) {
                        val uniform = maxOf(leftPaddingPx + mapPaddingPx, topPaddingPx + mapPaddingPx, rightPaddingPx + mapPaddingPx, bottomPaddingPx + mapPaddingPx)
                        val update = org.maplibre.android.camera.CameraUpdateFactory.newLatLngBounds(bounds, uniform)
                        if (animate) m?.animateCamera(update) else m?.moveCamera(update)
                    }
                }
            }
        }
    }

    private fun computeRouteSignature(route: List<MapPoint>): String? {
        if (route.isEmpty()) return null
        val first = route.first()
        val last = route.last()
        return buildString {
            append(route.size).append(':')
            append(first.lat).append(',').append(first.lng).append('-')
            append(last.lat).append(',').append(last.lng)
        }
    }

    override fun updateMarkersOnMap(
        locations: List<MapPoint>,
        carArrivesInMinutes: Int?,
        orderEndsInMinutes: Int?,
        orderStatus: OrderStatus?,
        hasOrder: Boolean
    ) {
        markers.forEach { it.remove() }
        markers.clear()

        iconManager.createMarkerIcons(carArrivesInMinutes, orderEndsInMinutes, hasOrder)

        if (locations.isNotEmpty()) {
            drawAllMarkers(
                locations = locations,
                orderStatus = orderStatus,
                hasDestination = locations.size > 1
            )
        }
    }

    override fun updateDriverOnMap(driver: Executor?) {
        if (driver == null) {
            driverMarkers.forEach { it.remove() }
            driverMarkers.clear()
            lastDriverPosition = null
            lastDriverHeading = null
            return
        }

        val currentPos = Pair(driver.lat, driver.lng)
        val currentHeading = driver.heading.toFloat()

        if (lastDriverPosition != currentPos || lastDriverHeading != currentHeading) {
            if (driverMarkers.isNotEmpty()) {
                val m = driverMarkers.first()
                m.position = LatLng(driver.lat, driver.lng)
            } else {
                drawDriver(driver)
            }
            lastDriverPosition = currentPos
            lastDriverHeading = currentHeading
        }
    }

    override fun updateDriversOnMap(
        drivers: List<Executor>,
        hasOrder: Boolean
    ) {
        if (drivers.isEmpty() || hasOrder) {
            driverMarkersById.values.forEach { it.remove() }
            driverMarkersById.clear()
            lastDriverPosById.clear()
            lastDriverHeadingById.clear()
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
            val currentPos = Pair(driver.lat, driver.lng)
            val currentHeading = driver.heading.toFloat()
            val existing = driverMarkersById[driver.id]

            val positionChanged = lastDriverPosById[driver.id] != currentPos
            val headingChanged = lastDriverHeadingById[driver.id] != currentHeading

            if (existing == null) {
                val marker = map?.addMarker(
                    MarkerOptions()
                        .position(LatLng(driver.lat, driver.lng))
                        .icon(iconManager.requireDriverIcon() as org.maplibre.android.annotations.Icon)
                )
                if (marker != null) {
                    driverMarkersById[driver.id] = marker
                    lastDriverPosById[driver.id] = currentPos
                    lastDriverHeadingById[driver.id] = currentHeading
                }
            } else if (positionChanged || headingChanged) {
                existing.position = LatLng(driver.lat, driver.lng)
                lastDriverPosById[driver.id] = currentPos
                lastDriverHeadingById[driver.id] = currentHeading
            }
        }
    }

    override fun drawDriver(driver: Executor) {
        val marker = map?.addMarker(
            MarkerOptions()
                .position(LatLng(driver.lat, driver.lng))
                .icon(iconManager.requireDriverIcon() as org.maplibre.android.annotations.Icon)
        )
        if (marker != null) driverMarkers.add(marker)
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

        val latLngs = route.map { LatLng(it.lat, it.lng) }
        val color = iconManager.color(R.color.map_polyline_libre)

        polyline = map?.addPolyline(
            PolylineOptions()
                .addAll(latLngs)
                .color(color)
                .width(LMapConstants.POLYLINE_WIDTH)
        )

        // Fit bounds respecting padding if possible
        val boundsBuilder = LatLngBounds.Builder()
        latLngs.forEach { boundsBuilder.include(it) }
        locations.forEach { boundsBuilder.include(LatLng(it.lat, it.lng)) }

        val bounds = try {
            boundsBuilder.build()
        } catch (_: Throwable) {
            null
        }
        if (bounds != null && orderStatus !in OrderStatus.nonInteractive) {
            try {
                // Combine general map padding with edge paddings to match Google behavior
                val left = leftPaddingPx + paddingPx
                val top = topPaddingPx + paddingPx
                val right = rightPaddingPx + paddingPx
                val bottom = bottomPaddingPx + paddingPx

                // Prefer precise per-side padding via getCameraForLatLngBounds if available
                val cam = map?.getCameraForLatLngBounds(
                    bounds,
                    intArrayOf(left, top, right, bottom)
                )
                if (cam != null) {
                    if (animate) map?.animateCamera(
                        org.maplibre.android.camera.CameraUpdateFactory.newCameraPosition(
                            cam
                        )
                    )
                    else map?.cameraPosition = cam
                } else {
                    // Fallback: uniform padding update using the largest side as a best effort
                    val uniform = maxOf(left, top, right, bottom)
                    val update = org.maplibre.android.camera.CameraUpdateFactory.newLatLngBounds(bounds, uniform)
                    if (animate) map?.animateCamera(update) else map?.moveCamera(update)
                }
            } catch (_: Throwable) {
                // Fallback: approximate via getCameraForLatLngBounds without padding
                val cam = map?.getCameraForLatLngBounds(bounds)
                if (cam != null) map?.cameraPosition = cam
            }
        }
    }

    override fun drawAllMarkers(
        locations: List<MapPoint>,
        orderStatus: OrderStatus?,
        hasDestination: Boolean
    ) {
        if (locations.isEmpty()) return

        locations.forEachIndexed { index, p ->
            val mo = MarkerOptions().position(LatLng(p.lat, p.lng))
            when (index) {
                0 -> {
                    if (orderStatus != null || hasDestination) {
                        val icon = (iconManager.originInfoIcon
                            ?: iconManager.requireOriginIcon()) as org.maplibre.android.annotations.Icon
                        mo.icon(icon)
                        map?.addMarker(mo)?.let { markers.add(it) }
                    }
                }

                locations.lastIndex -> {
                    val icon = (iconManager.destinationInfoIcon
                        ?: iconManager.requireDestinationIcon()) as org.maplibre.android.annotations.Icon
                    mo.icon(icon)
                    map?.addMarker(mo)?.let { markers.add(it) }
                }

                else -> {
                    val icon = iconManager.requireMiddleIcon() as org.maplibre.android.annotations.Icon
                    mo.icon(icon)
                    map?.addMarker(mo)?.let { markers.add(it) }
                }
            }
        }
    }

    private fun applyAnchorIfPossible(options: MarkerOptions, u: Float, v: Float) {
        // Try common method names via reflection; ignore if not supported by the SDK version
        try {
            val m =
                options.javaClass.getMethod("anchor", Float::class.javaPrimitiveType, Float::class.javaPrimitiveType)
            m.invoke(options, u, v)
            return
        } catch (_: Throwable) {
        }
        try {
            val m =
                options.javaClass.getMethod("setAnchor", Float::class.javaPrimitiveType, Float::class.javaPrimitiveType)
            m.invoke(options, u, v)
            return
        } catch (_: Throwable) {
        }
        // Some versions expose pixel offsets instead; without icon size we skip to avoid misplacement
    }

    override fun drawDashedConnections(
        locations: List<MapPoint>,
        route: List<MapPoint>
    ) {
        val m = map ?: return
        locations.forEachIndexed { index, loc ->
            val target: MapPoint? = when (index) {
                0 -> route.firstOrNull()
                locations.lastIndex -> route.lastOrNull()
                else -> findClosestPointOnRoute(loc, route)
            }
            if (target != null && target != loc) {
                val dashed = m.addPolyline(
                    PolylineOptions()
                        .add(LatLng(loc.lat, loc.lng), LatLng(target.lat, target.lng))
                        .color(iconManager.color(R.color.map_dashed_polyline_libre))
                        .width(LMapConstants.DASHED_POLYLINE_WIDTH)
                    // MapLibre's classic annotations don't support Dash/Gaps directly.
                    // To truly dash, migrate to a LineLayer with a dash array in the style later.
                )
                dashedPolylines.add(dashed)
            }
        }
    }

    override fun findClosestPointOnRoute(location: MapPoint, route: List<MapPoint>): MapPoint? {
        if (route.isEmpty()) return null
        return route.minByOrNull { rp ->
            val dLat = location.lat - rp.lat
            val dLng = location.lng - rp.lng
            dLat * dLat + dLng * dLng
        }
    }

    // --- Cross‑platform map operations ---
    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        map?.setPadding(left, top, right, bottom)
    }

    override fun moveTo(point: MapPoint, zoom: Float?) {
        // MapLibreMap's classic API does not support zoom in moveCamera directly here
        map?.moveCamera(org.maplibre.android.camera.CameraUpdateFactory.newLatLng(LatLng(point.lat, point.lng)))
    }

    override fun animateTo(point: MapPoint) {
        map?.animateCamera(org.maplibre.android.camera.CameraUpdateFactory.newLatLng(LatLng(point.lat, point.lng)))
    }

    override fun setGesturesEnabled(enabled: Boolean) {
        // No-op: gestures configuration can be managed via UI settings if needed later
    }

    override fun setOnCameraIdle(listener: (MapPoint) -> Unit) {
        map?.addOnCameraIdleListener {
            val t = map?.cameraPosition?.target ?: return@addOnCameraIdleListener
            listener(MapPoint(t.latitude, t.longitude))
        }
    }

    override fun setOnCameraMoveStarted(listener: (isByUser: Boolean) -> Unit) {
        map?.addOnCameraMoveStartedListener { reason ->
            val isByUser = reason == MapLibreMap.OnCameraMoveStartedListener.REASON_API_GESTURE
            listener(isByUser)
        }
    }

    override fun getCameraTarget(): MapPoint? {
        val t = map?.cameraPosition?.target ?: return null
        return MapPoint(t.latitude, t.longitude)
    }

    override fun zoomOut() {
        map?.animateCamera(org.maplibre.android.camera.CameraUpdateFactory.zoomOut())
    }
}
