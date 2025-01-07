package uz.ildam.technologies.yalla.android.ui.sheets.select_from_map

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.dgis.sdk.Duration
import ru.dgis.sdk.map.CameraState
import uz.ildam.technologies.yalla.android.utils.getCurrentLocation
import uz.ildam.technologies.yalla.android2gis.CameraPosition
import uz.ildam.technologies.yalla.android2gis.GeoPoint
import uz.ildam.technologies.yalla.android2gis.MapView
import uz.ildam.technologies.yalla.android2gis.Zoom
import uz.ildam.technologies.yalla.android2gis.lat
import uz.ildam.technologies.yalla.android2gis.lon
import uz.ildam.technologies.yalla.android2gis.rememberCameraState
import uz.ildam.technologies.yalla.core.domain.model.MapPoint
import uz.ildam.technologies.yalla.android2gis.CameraState as ComposableCameraState

class ConcreteGisMap : MapStrategy {
    override val isMarkerMoving: MutableState<Boolean> = mutableStateOf(false)
    override val mapPoint: MutableState<MapPoint> = mutableStateOf(MapPoint(0.0, 0.0))
    private lateinit var context: Context
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var cameraState: ComposableCameraState

    @Composable
    override fun Map(
        modifier: Modifier,
    ) {
        context = LocalContext.current
        coroutineScope = rememberCoroutineScope()
        cameraState = rememberCameraState(CameraPosition(GeoPoint(0.0, 0.0), Zoom(2.0f)))
        val cameraNode by cameraState.node.collectAsState()

        LaunchedEffect(cameraState.position.point) {
            mapPoint.value = MapPoint(
                cameraState.position.point.lat,
                cameraState.position.point.lon
            )
        }

        MapView(
            modifier = modifier,
            cameraState = cameraState
        )

        DisposableEffect(cameraNode?.dgisCamera?.position) {
            val closable = cameraNode?.stateChannel?.connect { state ->
                isMarkerMoving.value = (state != CameraState.FREE)
            }
            onDispose { closable?.close() }
        }
    }

    override fun move(to: MapPoint) {
        if (::cameraState.isInitialized) {
            coroutineScope.launch {
                cameraState.move(
                    duration = Duration.ZERO,
                    position = CameraPosition(
                        point = GeoPoint(to.lat, to.lng),
                        zoom = Zoom(15f)
                    )
                )
            }
        }
    }

    override fun animate(to: MapPoint, durationMillis: Int) {
        if (::cameraState.isInitialized) {
            coroutineScope.launch {
                cameraState.move(
                    duration = Duration.ofMilliseconds(durationMillis.toLong()),
                    position = CameraPosition(
                        point = GeoPoint(to.lat, to.lng),
                        zoom = Zoom(15f)
                    )
                )
            }
        }
    }

    override fun moveToMyLocation() {
        if (::cameraState.isInitialized) {
            getCurrentLocation(context) { location ->
                val mapPoint = location.let { MapPoint(it.latitude, it.longitude) }
                move(mapPoint)
            }
        }
    }

    override fun animateToMyLocation(durationMillis: Int) {
        if (::cameraState.isInitialized) {
            getCurrentLocation(context) { location ->
                val mapPoint = MapPoint(location.latitude, location.longitude)
                animate(mapPoint, durationMillis)
            }
        }
    }
}