package uz.ildam.technologies.yalla.android.ui.screens.map

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.dgis.sdk.Duration
import ru.dgis.sdk.geometry.ComplexGeometry
import ru.dgis.sdk.geometry.PointGeometry
import ru.dgis.sdk.map.Zoom
import ru.dgis.sdk.map.calcPosition
import uz.ildam.technologies.yalla.android2gis.CameraNode
import uz.ildam.technologies.yalla.android2gis.CameraState
import uz.ildam.technologies.yalla.android2gis.GeoPoint
import uz.ildam.technologies.yalla.android2gis.dpToPx
import uz.ildam.technologies.yalla.core.data.enums.MapType
import uz.ildam.technologies.yalla.android2gis.CameraPosition as GisCameraPosition

class MapActionHandler(
    private val context: Context,
    private val mapType: MapType,
    private val googleCameraState: CameraPositionState,
    private val gisCameraState: CameraState,
    private val gisCameraNode: State<CameraNode?>
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

    fun moveCameraToFitBounds(
        routing: List<MapPoint>,
        animate: Boolean = false
    ) = scope.launch {
        if (routing.isNotEmpty()) {
            if (mapType == MapType.Google) {
                val boundsBuilder = LatLngBounds.Builder()
                routing.forEach { boundsBuilder.include(LatLng(it.lat, it.lng)) }
                val bounds = boundsBuilder.build()
                if (animate) googleCameraState.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds, dpToPx(context, 100)),
                    durationMs = defaultAnimationDuration
                ) else googleCameraState.animate(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        dpToPx(context, 100)
                    )
                )
            } else {
                gisCameraNode.value?.dgisCamera?.let { camera ->
                    val list = routing.map { PointGeometry(GeoPoint(it.lat, it.lng)) }
                    val geometry = ComplexGeometry(list)
                    val position = calcPosition(camera, geometry)

                    gisCameraState.move(
                        position = position,
                        duration = Duration.ofMilliseconds(defaultAnimationDuration.toLong())
                    )
                }
            }
        }
    }
}