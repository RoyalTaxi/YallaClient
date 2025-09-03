package uz.yalla.client.core.common.maps.google.controller

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import uz.yalla.client.core.common.maps.core.controller.MapController
import uz.yalla.client.core.common.maps.google.util.GMapConstants
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.domain.model.MapPoint

class GMapController : MapController {
    private var map: GoogleMap? = null
    private var padLeft: Int = 0
    private var padTop: Int = 0
    private var padRight: Int = 0
    private var padBottom: Int = 0

    override fun attachMap(map: Any) {
        this.map = map as? GoogleMap
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        padLeft = left
        padTop = top
        padRight = right
        padBottom = bottom
        map?.setPadding(left, top, right, bottom)
    }

    override fun moveTo(point: MapPoint, zoom: Float?) {
        map?.let { g ->
            val z = zoom ?: GMapConstants.DEFAULT_ZOOM
            g.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(point.lat, point.lng), z))
        }
    }

    override fun animateTo(point: MapPoint) {
        map?.animateCamera(CameraUpdateFactory.newLatLng(LatLng(point.lat, point.lng)))
    }

    override fun zoomOut() {
        val currentZoom = map?.cameraPosition?.zoom ?: return
        if (currentZoom > GMapConstants.MIN_ZOOM) {
            map?.animateCamera(CameraUpdateFactory.zoomOut())
        }
    }

    override fun setGesturesEnabled(enabled: Boolean) {
        map?.uiSettings?.apply {
            // Allow pan/zoom toggling via this flag
            isScrollGesturesEnabled = enabled
            isZoomGesturesEnabled = enabled
            // Always keep tilt/rotate disabled
            isTiltGesturesEnabled = false
            isRotateGesturesEnabled = false
        }
    }

    override fun setOnCameraIdle(listener: (MapPoint) -> Unit) {
        map?.setOnCameraIdleListener {
            val t = map?.cameraPosition?.target ?: return@setOnCameraIdleListener
            listener(MapPoint(t.latitude, t.longitude))
        }
    }

    override fun setOnCameraMoveStarted(listener: (isByUser: Boolean) -> Unit) {
        map?.setOnCameraMoveStartedListener { reason ->
            val isByUser = reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE || reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION
            listener(isByUser)
        }
    }

    override fun getCameraTarget(): MapPoint? {
        val t = map?.cameraPosition?.target ?: return null
        return MapPoint(t.latitude, t.longitude)
    }

    override fun moveWithoutZoom(point: MapPoint) {
        val currentZoom = map?.cameraPosition?.zoom
        val z = currentZoom ?: GMapConstants.DEFAULT_ZOOM
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(point.lat, point.lng), z))
    }

    override fun moveToMyLocation(context: Context) {
        getCurrentLocation(context) { loc ->
            moveTo(MapPoint(loc.latitude, loc.longitude), null)
        }
    }

    override fun animateToMyLocation(context: Context, durationMillis: Int) {
        getCurrentLocation(context) { loc ->
            animateTo(MapPoint(loc.latitude, loc.longitude))
        }
    }

    override fun moveToFitBounds(routing: List<MapPoint>) {
        if (routing.isEmpty()) return
        val builder = com.google.android.gms.maps.model.LatLngBounds.Builder()
        routing.forEach { builder.include(LatLng(it.lat, it.lng)) }
        val bounds = builder.build()
        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
    }

    override fun animateToFitBounds(routing: List<MapPoint>) {
        if (routing.isEmpty()) return
        val builder = com.google.android.gms.maps.model.LatLngBounds.Builder()
        routing.forEach { builder.include(LatLng(it.lat, it.lng)) }
        val bounds = builder.build()
        map?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
    }

    override fun configureDefaultSettings(context: Context) {
        map?.uiSettings?.apply {
            isCompassEnabled = false
            isMapToolbarEnabled = false
            isZoomControlsEnabled = false
            isMyLocationButtonEnabled = false
            isTiltGesturesEnabled = false
            isRotateGesturesEnabled = false
            isScrollGesturesEnabledDuringRotateOrZoom = false
        }

        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fine || coarse) {
            try { map?.isMyLocationEnabled = true } catch (_: Exception) {}
        }
    }
}
