package uz.yalla.client.core.common.maps.google.util

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import androidx.annotation.RequiresPermission
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import uz.yalla.client.core.domain.model.MapPoint


fun isNightMode(context: Context): Boolean {
    val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
}

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun GoogleMap.setUp() {
    uiSettings.isCompassEnabled = false
    uiSettings.isMyLocationButtonEnabled = false
    uiSettings.isMapToolbarEnabled = false
    uiSettings.isRotateGesturesEnabled = true
    uiSettings.isTiltGesturesEnabled = false
    uiSettings.isZoomControlsEnabled = false
    uiSettings.isZoomGesturesEnabled = true
    uiSettings.isScrollGesturesEnabled = true
    uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = false

    mapType = GoogleMap.MAP_TYPE_NORMAL

    isIndoorEnabled = false
    isTrafficEnabled = false
    isBuildingsEnabled = false
    isMyLocationEnabled = true
}

fun GoogleMap.moveTo(
    point: MapPoint,
    zoom: Float? = null
) {
    zoom?.let {
        moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(point.lat, point.lng), zoom))
    } ?: run {
        moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(point.lat, point.lng),
                GMapConstants.DEFAULT_ZOOM
            )
        )
    }
}
