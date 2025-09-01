package uz.yalla.client.core.common.maps.libre.controller

import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import uz.yalla.client.core.common.maps.core.controller.MapController
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.domain.model.MapPoint

class LMapController : MapController {
    private var map: MapLibreMap? = null
    private var padLeft: Int = 0
    private var padTop: Int = 0
    private var padRight: Int = 0
    private var padBottom: Int = 0

    override fun attachMap(map: Any) {
        this.map = map as? MapLibreMap
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        padLeft = left
        padTop = top
        padRight = right
        padBottom = bottom
        @Suppress("DEPRECATION")
        map?.setPadding(left, top, right, bottom)
    }

    override fun moveTo(point: MapPoint, zoom: Float?) {
        val z = (zoom ?: 15f).toDouble()
        val target = LatLng(point.lat, point.lng)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(target, z))
    }

    override fun animateTo(point: MapPoint) {
        val target = LatLng(point.lat, point.lng)
        map?.animateCamera(CameraUpdateFactory.newLatLng(target))
    }

    override fun zoomOut() {
        map?.animateCamera(CameraUpdateFactory.zoomOut())
    }

    override fun setGesturesEnabled(enabled: Boolean) {
        // Not exposed uniformly via classic MapLibreMap; skip toggling here.
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

    override fun moveWithoutZoom(point: MapPoint) {
        // newLatLng keeps current zoom level in MapLibre classic
        map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(point.lat, point.lng)))
    }

    override fun moveToMyLocation(context: android.content.Context) {
        getCurrentLocation(context) { loc ->
            moveTo(MapPoint(loc.latitude, loc.longitude), null)
        }
    }

    override fun animateToMyLocation(context: android.content.Context, durationMillis: Int) {
        getCurrentLocation(context) { loc ->
            animateTo(MapPoint(loc.latitude, loc.longitude))
        }
    }

    override fun moveToFitBounds(routing: List<MapPoint>) {
        if (routing.isEmpty()) return
        val builder = org.maplibre.android.geometry.LatLngBounds.Builder()
        routing.forEach { builder.include(LatLng(it.lat, it.lng)) }
        val bounds = try { builder.build() } catch (_: Throwable) { null }
        bounds?.let { b ->
            val cam = map?.getCameraForLatLngBounds(b, intArrayOf(padLeft, padTop, padRight, padBottom))
            if (cam != null) map?.cameraPosition = cam
        }
    }

    override fun animateToFitBounds(routing: List<MapPoint>) {
        if (routing.isEmpty()) return
        val builder = org.maplibre.android.geometry.LatLngBounds.Builder()
        routing.forEach { builder.include(LatLng(it.lat, it.lng)) }
        val bounds = try { builder.build() } catch (_: Throwable) { null }
        bounds?.let { b ->
            val cam = map?.getCameraForLatLngBounds(b, intArrayOf(padLeft, padTop, padRight, padBottom))
            if (cam != null) map?.animateCamera(CameraUpdateFactory.newCameraPosition(cam))
        }
    }

    override fun configureDefaultSettings(context: android.content.Context) {
        // MapLibre classic has limited global UI settings via MapLibreMap; keep defaults.
    }
}
