package uz.ildam.technologies.yalla.android.ui.screens.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.dgis.sdk.Duration
import ru.dgis.sdk.coordinates.Bearing
import ru.dgis.sdk.map.Tilt
import ru.dgis.sdk.map.Zoom
import uz.ildam.technologies.yalla.android2gis.CameraState
import uz.ildam.technologies.yalla.android2gis.GeoPoint
import uz.ildam.technologies.yalla.android2gis.lat
import uz.ildam.technologies.yalla.android2gis.lon
import uz.ildam.technologies.yalla.core.data.enums.MapType
import uz.ildam.technologies.yalla.android2gis.CameraPosition as GisCameraPosition

class MapActionHandler(
    private val mapType: MapType,
    private val googleCameraState: CameraPositionState,
    private val gisCameraState: CameraState
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val defaultAnimationDuration = 1000
    private val defaultZoomLevel = 15f

    fun moveCamera(
        mapPoint: MapPoint,
        animate: Boolean = false,
        duration: Int = 1
    ) = scope.launch {
        if (mapType == MapType.Google) {
            googleCameraState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(
                        LatLng(mapPoint.lat, mapPoint.lng),
                        defaultZoomLevel,
                        0f,
                        0f
                    )
                ),
                durationMs = if (animate) defaultAnimationDuration else duration
            )
        } else gisCameraState.move(
            duration = if (animate) Duration.ofMilliseconds(defaultAnimationDuration.toLong()) else Duration.ZERO,
            position = GisCameraPosition(
                GeoPoint(mapPoint.lat, mapPoint.lng),
                Zoom(defaultZoomLevel)
            )
        )
    }

    fun getCameraPosition(): MapPoint {
        return if (mapType == MapType.Gis)
            gisCameraState.position.point.let { MapPoint(it.lat, it.lon) }
        else
            googleCameraState.position.target.let { MapPoint(it.latitude, it.longitude) }
    }

    private fun estimateZoomLevel(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Float {
        val latDiff = maxLat - minLat
        val lngDiff = maxLng - minLng

        return when {
            latDiff > 1 || lngDiff > 1 -> 10f
            latDiff > 0.5 || lngDiff > 0.5 -> 12f
            else -> 14f
        }
    }

    fun moveCameraToFitBounds(
        routing: List<MapPoint>,
        animate: Boolean = false
    ) = scope.launch {
        if (routing.isNotEmpty()) {
            val minLatitude = routing.minOf { it.lat }
            val maxLatitude = routing.maxOf { it.lat }
            val minLongitude = routing.minOf { it.lng }
            val maxLongitude = routing.maxOf { it.lng }

            val latRange = maxLatitude - minLatitude
            val lonRange = maxLongitude - minLongitude

            val paddedMinLatitude = minLatitude - latRange
            val paddedMaxLatitude = maxLatitude + latRange
            val paddedMinLongitude = minLongitude - lonRange
            val paddedMaxLongitude = maxLongitude + lonRange

            val centerLatitude = (paddedMinLatitude + paddedMaxLatitude) / 2
            val centerLongitude = (paddedMinLongitude + paddedMaxLongitude) / 2
            val centerPoint = GeoPoint(latitude = centerLatitude, longitude = centerLongitude)

            if (mapType == MapType.Google) {
                val boundsBuilder = LatLngBounds.Builder()
                routing.forEach { boundsBuilder.include(LatLng(it.lat, it.lng)) }
                val bounds = boundsBuilder.build()
                if (animate)
                    googleCameraState.animate(
                        CameraUpdateFactory.newLatLngBounds(bounds, 200),
                        durationMs = defaultAnimationDuration
                    )
                else
                    googleCameraState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 200))
            } else {
                gisCameraState.move(
                    position = GisCameraPosition(
                        point = centerPoint,
                        zoom = Zoom(
                            estimateZoomLevel(
                                paddedMinLatitude,
                                paddedMaxLatitude,
                                paddedMinLongitude,
                                paddedMaxLongitude
                            ) - 2f
                        ),
                        tilt = Tilt(0f),
                        bearing = Bearing(0.0)
                    ),
                    duration = if (animate) Duration.ofMilliseconds(defaultAnimationDuration.toLong()) else Duration.ZERO
                )
            }
        }
    }
}