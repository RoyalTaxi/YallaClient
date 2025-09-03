@file:Suppress("DEPRECATION")
package uz.yalla.client.core.common.maps.libre.manager
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import android.graphics.Color
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory.iconAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.iconAnchor
import org.maplibre.android.style.layers.PropertyFactory.iconImage
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineDasharray
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.Icon
import org.maplibre.android.annotations.IconFactory
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.maps.core.manager.MapElementManager
import uz.yalla.client.core.common.maps.libre.util.LMapConstants
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus

class LMapElementManager(
    private val lIconManager: LMapIconManager
) : MapElementManager(lIconManager) {

    private var map: MapLibreMap? = null

    // Annotation markers overlay
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
        // Reset feature collections on all sources (if style is available)
        withStyle { style ->
            style.getSourceAs<GeoJsonSource>(SRC_ROUTE)?.setGeoJson(EMPTY_FC)
            style.getSourceAs<GeoJsonSource>(SRC_DASHED)?.setGeoJson(EMPTY_FC)
            style.getSourceAs<GeoJsonSource>(SRC_DRIVER)?.setGeoJson(EMPTY_FC)
            style.getSourceAs<GeoJsonSource>(SRC_DRIVERS)?.setGeoJson(EMPTY_FC)
        }

        // Remove annotation markers
        markers.forEach { it.remove() }
        markers.clear()
        driverMarkers.forEach { it.remove() }
        driverMarkers.clear()
        driverMarkersById.values.forEach { it.remove() }
        driverMarkersById.clear()

        lastDriverPosition = null
        lastDriverHeading = null
        lastDriverPosById.clear()
        lastDriverHeadingById.clear()
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

        withStyle { style ->
            ensureImages(style)
            ensureSourcesAndLayers(style)

            // Ensure route color respects theme (white on dark, black on light)
            val routeColor = if (isDarkTheme) Color.WHITE else Color.BLACK
            (style.getLayer(LYR_ROUTE) as? LineLayer)?.setProperties(
                lineColor(routeColor)
            )

            if (routeChanged) {
                // Update main route geometry
                val fc = if (route.size >= 2) lineStringFc(route) else EMPTY_FC
                style.getSourceAs<GeoJsonSource>(SRC_ROUTE)?.setGeoJson(fc)

                // Update dashed connections
                style.getSourceAs<GeoJsonSource>(SRC_DASHED)?.setGeoJson(dashedLinesFc(locations, route))

                lastRouteSignature = currentSignature
            }
        }

        // Adjust camera similar to old logic
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
        lIconManager.createMarkerIcons(carArrivesInMinutes, orderEndsInMinutes, hasOrder)
        map?.getStyle {
            // Clear previous markers
            markers.forEach { it.remove() }
            markers.clear()

            val factory = IconFactory.getInstance(lIconManager.appContext())
            val iconOrigin = factory.fromBitmap(lIconManager.requireOriginBitmap())
            val iconMiddle = factory.fromBitmap(lIconManager.requireMiddleBitmap())
            val iconDest = factory.fromBitmap(lIconManager.requireDestinationBitmap())
            val iconOriginInfo = lIconManager.currentOriginInfoBitmap()?.let { factory.fromBitmap(it) }
            val iconDestInfo = lIconManager.currentDestinationInfoBitmap()?.let { factory.fromBitmap(it) }

            locations.forEachIndexed { index, p ->
                val latLng = LatLng(p.lat, p.lng)
                val icon: Icon? = when (index) {
                    0 -> if (orderStatus != null || locations.size > 1) (iconOriginInfo ?: iconOrigin) else null
                    locations.lastIndex -> (iconDestInfo ?: iconDest)
                    else -> iconMiddle
                }
                if (icon != null) {
                    val mo = MarkerOptions().position(latLng).icon(icon)
                    map?.addMarker(mo)?.let { markers.add(it) }
                }
            }
        }
    }

    override fun updateDriverOnMap(driver: Executor?) {
        map?.getStyle {
            if (driver == null) {
                driverMarkers.forEach { it.remove() }
                driverMarkers.clear()
                lastDriverPosition = null
                lastDriverHeading = null
                return@getStyle
            }

            val currentPos = Pair(driver.lat, driver.lng)
            val currentHeading = driver.heading.toFloat()
            val factory = IconFactory.getInstance(lIconManager.appContext())
            val iconDriver = factory.fromBitmap(lIconManager.driverBitmapRotated(currentHeading))

            if (driverMarkers.isEmpty()) {
                val mo = MarkerOptions()
                    .position(LatLng(driver.lat, driver.lng))
                    .icon(iconDriver)
                map?.addMarker(mo)?.let { driverMarkers.add(it) }
            } else {
                // Recreate to apply rotation via rotated bitmap
                driverMarkers.first().remove()
                driverMarkers.clear()
                val mo = MarkerOptions().position(LatLng(driver.lat, driver.lng)).icon(iconDriver)
                map?.addMarker(mo)?.let { driverMarkers.add(it) }
            }
            lastDriverPosition = currentPos
            lastDriverHeading = currentHeading
        }
    }

    override fun updateDriversOnMap(
        drivers: List<Executor>,
        hasOrder: Boolean
    ) {
        map?.getStyle {
            if (drivers.isEmpty() || hasOrder) {
                driverMarkersById.values.forEach { it.remove() }
                driverMarkersById.clear()
                lastDriverPosById.clear()
                lastDriverHeadingById.clear()
                return@getStyle
            }

            val factory = IconFactory.getInstance(lIconManager.appContext())
            // icon per driver heading
            val aliveIds = drivers.map { it.id }.toSet()
            val idsToRemove = driverMarkersById.keys.filter { it !in aliveIds }
            idsToRemove.forEach { id -> driverMarkersById.remove(id)?.remove() }

            drivers.forEach { d ->
                val currentPos = Pair(d.lat, d.lng)
                val currentHeading = d.heading.toFloat()
                val existing = driverMarkersById[d.id]
                if (existing == null) {
                    val icon = factory.fromBitmap(lIconManager.driverBitmapRotated(currentHeading))
                    val mo = MarkerOptions()
                        .position(LatLng(d.lat, d.lng))
                        .icon(icon)
                    map?.addMarker(mo)?.let { m -> driverMarkersById[d.id] = m }
                } else {
                    // Recreate marker to apply new rotation reliably in all SDK versions
                    existing.remove()
                    val icon = factory.fromBitmap(lIconManager.driverBitmapRotated(currentHeading))
                    val mo = MarkerOptions().position(LatLng(d.lat, d.lng)).icon(icon)
                    map?.addMarker(mo)?.let { m -> driverMarkersById[d.id] = m }
                }
                lastDriverPosById[d.id] = currentPos
                lastDriverHeadingById[d.id] = currentHeading
            }
        }
    }

    override fun drawDriver(driver: Executor) {
        // Covered by updateDriverOnMap in the layer-based approach
        updateDriverOnMap(driver)
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
        updateRouteOnMap(route, locations, paddingPx, leftPaddingPx, topPaddingPx, rightPaddingPx, bottomPaddingPx, isDarkTheme, orderStatus, animate)
    }

    override fun drawAllMarkers(
        locations: List<MapPoint>,
        orderStatus: OrderStatus?,
        hasDestination: Boolean
    ) {
        // Covered by updateMarkersOnMap in the layer-based approach
        updateMarkersOnMap(locations, null, null, orderStatus, hasDestination)
    }

    override fun drawDashedConnections(
        locations: List<MapPoint>,
        route: List<MapPoint>
    ) {
        withStyle { style ->
            ensureSourcesAndLayers(style)
            style.getSourceAs<GeoJsonSource>(SRC_DASHED)?.setGeoJson(dashedLinesFc(locations, route))
        }
    }

    private fun dashedLinesFc(locations: List<MapPoint>, route: List<MapPoint>): String {
        val feats = mutableListOf<String>()
        locations.forEachIndexed { index, loc ->
            val target: MapPoint? = when (index) {
                0 -> route.firstOrNull()
                locations.lastIndex -> route.lastOrNull()
                else -> findClosestPointOnRoute(loc, route)
            }
            if (target != null && target != loc) {
                feats.add(lineFeature(listOf(loc.lng to loc.lat, target.lng to target.lat)))
            }
        }
        return featureCollection(feats)
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
        @Suppress("DEPRECATION")
        map?.setPadding(left, top, right, bottom)
    }

    override fun moveTo(point: MapPoint, zoom: Float?) {
        map?.moveCamera(
            org.maplibre.android.camera.CameraUpdateFactory.newLatLng(
                LatLng(point.lat, point.lng)
            )
        )
    }

    override fun animateTo(point: MapPoint) {
        map?.animateCamera(org.maplibre.android.camera.CameraUpdateFactory.newLatLng(LatLng(point.lat, point.lng)))
    }

    override fun setGesturesEnabled(enabled: Boolean) {
        // No-op for element manager
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

    // --- Style helpers ---
    private fun withStyle(block: (Style) -> Unit) {
        val m = map ?: return
        try {
            m.getStyle { style -> block(style) }
        } catch (_: Throwable) {
            // Ignore if style not ready
        }
    }

    private fun ensureImages(style: Style) {
        // Core icons
        addOrUpdateImage(style, IMG_DRIVER, lIconManager.requireDriverBitmap())
        addOrUpdateImage(style, IMG_ORIGIN, lIconManager.requireOriginBitmap())
        addOrUpdateImage(style, IMG_MIDDLE, lIconManager.requireMiddleBitmap())
        addOrUpdateImage(style, IMG_DEST, lIconManager.requireDestinationBitmap())

        // Info marker variants (if available)
        lIconManager.currentOriginInfoBitmap()?.let { addOrUpdateImage(style, IMG_ORIGIN_INFO, it) }
        lIconManager.currentDestinationInfoBitmap()?.let { addOrUpdateImage(style, IMG_DEST_INFO, it) }
    }

    private fun addOrUpdateImage(style: Style, id: String, bitmap: android.graphics.Bitmap) {
        try {
            style.addImage(id, bitmap)
        } catch (_: Throwable) {
            // Try override if already exists
            try { style.addImage(id, bitmap) } catch (_: Throwable) {}
        }
    }

    private fun ensureSourcesAndLayers(style: Style) {
        // Sources
        if (style.getSource(SRC_ROUTE) == null) style.addSource(GeoJsonSource(SRC_ROUTE))
        if (style.getSource(SRC_DASHED) == null) style.addSource(GeoJsonSource(SRC_DASHED))
        if (style.getSource(SRC_DRIVER) == null) style.addSource(GeoJsonSource(SRC_DRIVER))
        if (style.getSource(SRC_DRIVERS) == null) style.addSource(GeoJsonSource(SRC_DRIVERS))

        // Layers (add if missing; order them so markers are above lines)
        if (style.getLayer(LYR_ROUTE) == null) {
            val layer = LineLayer(LYR_ROUTE, SRC_ROUTE).withProperties(
                lineWidth(LMapConstants.POLYLINE_WIDTH),
                lineColor(lIconManager.color(R.color.map_polyline_libre))
            )
            style.addLayer(layer)
        }
        if (style.getLayer(LYR_DASHED) == null) {
            val layer = LineLayer(LYR_DASHED, SRC_DASHED).withProperties(
                lineWidth(LMapConstants.DASHED_POLYLINE_WIDTH),
                lineColor(lIconManager.color(R.color.map_dashed_polyline_libre)),
                lineDasharray(arrayOf(2f, 2f))
            )
            style.addLayer(layer)
        }
        // No SymbolLayer for markers; using annotation API instead
        if (style.getLayer(LYR_DRIVER) == null) {
            val layer = SymbolLayer(LYR_DRIVER, SRC_DRIVER).withProperties(
                iconImage(Expression.coalesce(Expression.get(P_ICON), Expression.literal(IMG_DRIVER))),
                iconAllowOverlap(true),
                iconAnchor(Property.ICON_ANCHOR_CENTER),
                org.maplibre.android.style.layers.PropertyFactory.iconIgnorePlacement(true)
            )
            style.addLayer(layer)
        }
        if (style.getLayer(LYR_DRIVERS) == null) {
            val layer = SymbolLayer(LYR_DRIVERS, SRC_DRIVERS).withProperties(
                iconImage(Expression.coalesce(Expression.get(P_ICON), Expression.literal(IMG_DRIVER))),
                iconAllowOverlap(true),
                iconAnchor(Property.ICON_ANCHOR_CENTER),
                org.maplibre.android.style.layers.PropertyFactory.iconIgnorePlacement(true)
            )
            style.addLayer(layer)
        }
    }

    private companion object {
        const val EMPTY_FC = "{\"type\":\"FeatureCollection\",\"features\":[]}"
        const val SRC_ROUTE = "src_route"
        const val LYR_ROUTE = "layer_route"
        const val SRC_DASHED = "src_dashed"
        const val LYR_DASHED = "layer_dashed"
        const val SRC_DRIVER = "src_driver"
        const val LYR_DRIVER = "layer_driver"
        const val SRC_DRIVERS = "src_drivers"
        const val LYR_DRIVERS = "layer_drivers"

        const val P_ICON = "icon"

        const val IMG_DRIVER = "img_driver"
        const val IMG_ORIGIN = "img_origin"
        const val IMG_MIDDLE = "img_middle"
        const val IMG_DEST = "img_destination"
        const val IMG_ORIGIN_INFO = "img_origin_info"
        const val IMG_DEST_INFO = "img_destination_info"
    }

    // --- Minimal GeoJSON builders (avoid external dependency) ---
    private fun featureCollection(features: List<String>): String = buildString {
        append("{\"type\":\"FeatureCollection\",\"features\":[")
        features.forEachIndexed { i, f ->
            if (i > 0) append(',')
            append(f)
        }
        append("]}")
    }

    private fun pointFeature(lng: Double, lat: Double, icon: String): String =
        "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[$lng,$lat]},\"properties\":{\"$P_ICON\":\"$icon\"}}"

    private fun lineFeature(coords: List<Pair<Double, Double>>): String = buildString {
        append("{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[")
        coords.forEachIndexed { i, (x, y) ->
            if (i > 0) append(',')
            append('[').append(x).append(',').append(y).append(']')
        }
        append("]}}")
    }

    private fun lineStringFc(route: List<MapPoint>): String = featureCollection(
        listOf(lineFeature(route.map { it.lng to it.lat }))
    )
}
