package uz.ildam.technologies.yalla.android.ui.screens.map

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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.dgis.sdk.Duration
import ru.dgis.sdk.geometry.ComplexGeometry
import ru.dgis.sdk.geometry.PointGeometry
import ru.dgis.sdk.map.CameraState
import ru.dgis.sdk.map.calcPosition
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.utils.getCurrentLocation
import uz.ildam.technologies.yalla.android2gis.CameraPosition
import uz.ildam.technologies.yalla.android2gis.GeoPoint
import uz.ildam.technologies.yalla.android2gis.MapView
import uz.ildam.technologies.yalla.android2gis.Marker
import uz.ildam.technologies.yalla.android2gis.Polyline
import uz.ildam.technologies.yalla.android2gis.Zoom
import uz.ildam.technologies.yalla.android2gis.imageFromResource
import uz.ildam.technologies.yalla.android2gis.lat
import uz.ildam.technologies.yalla.android2gis.lon
import uz.ildam.technologies.yalla.android2gis.rememberCameraState
import uz.ildam.technologies.yalla.core.domain.model.MapPoint
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderStatus
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
        uiState: MapUIState
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
        ) {

            if (uiState.selectedDriver?.status == OrderStatus.Appointed) uiState.selectedLocation?.point?.let {
                Marker(
                    icon = imageFromResource(R.drawable.ic_origin_marker),
                    position = GeoPoint(it.lat, it.lng)
                )
            }


            if (uiState.route.isNotEmpty()) {
                Polyline(
                    points = uiState.route.map { GeoPoint(it.lat, it.lng) },
                    width = 4.dp
                )

                Marker(
                    icon = imageFromResource(R.drawable.ic_origin_marker),
                    position = GeoPoint(uiState.route.first().lat, uiState.route.first().lng)
                )

                uiState.destinations.dropLast(1).forEach { routePoint ->
                    routePoint.point?.let {
                        Marker(
                            icon = imageFromResource(R.drawable.ic_middle_marker),
                            position = GeoPoint(it.lat, it.lng)
                        )
                    }
                }

                Marker(
                    icon = imageFromResource(R.drawable.ic_destination_marker),
                    position = GeoPoint(uiState.route.last().lat, uiState.route.last().lng)
                )
            }

            uiState.selectedDriver?.let {
                Marker(
                    icon = imageFromResource(R.drawable.img_car_marker),
                    position = GeoPoint(
                        it.executor.coords.lat,
                        it.executor.coords.lng
                    )
                )
            }

            // Add car markers
            uiState.drivers.take(20).forEach { driver ->
                Marker(
                    icon = imageFromResource(R.drawable.img_car_marker), // Use car marker icon
                    position = GeoPoint(driver.lat, driver.lng), // Driver's position
                )
            }
        }

        DisposableEffect(cameraNode?.dgisCamera?.position) {
            val closable = cameraNode?.stateChannel?.connect { state ->
                isMarkerMoving.value = (state != CameraState.FREE)
            }
            onDispose { closable?.close() }
        }
    }

    override fun move(to: MapPoint) {
        if (::cameraState.isInitialized) coroutineScope.launch {
            cameraState.move(
                duration = Duration.ZERO,
                position = CameraPosition(
                    point = GeoPoint(to.lat, to.lng),
                    zoom = Zoom(15f)
                )
            )
        }
    }

    override fun animate(to: MapPoint, durationMillis: Int) {
        if (::cameraState.isInitialized) coroutineScope.launch {
            cameraState.move(
                duration = Duration.ofMilliseconds(durationMillis.toLong()),
                position = CameraPosition(
                    point = GeoPoint(to.lat, to.lng),
                    zoom = Zoom(15f)
                )
            )
        }
    }

    override fun moveToMyLocation() {
        if (::cameraState.isInitialized) getCurrentLocation(context) { location ->
            val mapPoint = location.let { MapPoint(it.latitude, it.longitude) }
            move(mapPoint)
        }
    }

    override fun animateToMyLocation(durationMillis: Int) {
        if (::cameraState.isInitialized) getCurrentLocation(context) { location ->
            val mapPoint = MapPoint(location.latitude, location.longitude)
            animate(mapPoint, durationMillis)
        }
    }

    override fun moveToFitBounds(routing: List<MapPoint>) {
        if (::cameraState.isInitialized) coroutineScope.launch {
            cameraState.node.value?.dgisCamera?.let { camera ->
                val list = routing.map { PointGeometry(GeoPoint(it.lat, it.lng)) }
                val geometry = ComplexGeometry(list)
                val position = calcPosition(camera, geometry)

                cameraState.move(
                    position = position,
                    duration = Duration.ZERO
                )
            }
        }
    }

    override fun animateToFitBounds(routing: List<MapPoint>) {
        if (::cameraState.isInitialized) coroutineScope.launch {
            cameraState.node.value?.dgisCamera?.let { camera ->
                val list = routing.map { PointGeometry(GeoPoint(it.lat, it.lng)) }
                val geometry = ComplexGeometry(list)
                val position = calcPosition(camera, geometry)

                cameraState.move(
                    position = position,
                    duration = Duration.ofMilliseconds(1000L)
                )
            }
        }
    }
}