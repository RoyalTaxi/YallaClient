package uz.yalla.client.core.common.map
//
//import android.content.Context
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.State
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.snapshots.SnapshotStateList
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.launch
//import ru.dgis.sdk.Duration
//import ru.dgis.sdk.geometry.ComplexGeometry
//import ru.dgis.sdk.geometry.PointGeometry
//import ru.dgis.sdk.map.CameraState
//import ru.dgis.sdk.map.calcPosition
//import uz.yalla.client.core.common.R
//import uz.yalla.client.core.common.utils.getCurrentLocation
//import uz.yalla.client.core.dgis.CameraPosition
//import uz.yalla.client.core.dgis.GeoPoint
//import uz.yalla.client.core.dgis.MapView
//import uz.yalla.client.core.dgis.Marker
//import uz.yalla.client.core.dgis.Polyline
//import uz.yalla.client.core.dgis.Zoom
//import uz.yalla.client.core.dgis.imageFromResource
//import uz.yalla.client.core.dgis.lat
//import uz.yalla.client.core.dgis.lon
//import uz.yalla.client.core.dgis.rememberCameraState
//import uz.yalla.client.core.domain.model.Executor
//import uz.yalla.client.core.domain.model.MapPoint
//import uz.yalla.client.core.domain.model.OrderStatus
//import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
//import uz.yalla.client.core.dgis.CameraState as ComposableCameraState
//
//class ConcreteGisMap : MapStrategy {
//    override val isMarkerMoving = MutableStateFlow(false)
//    override val mapPoint: MutableState<MapPoint> = mutableStateOf(MapPoint(0.0, 0.0))
//
//    private var driver: MutableState<Executor?> = mutableStateOf(null)
//    private val drivers: SnapshotStateList<Executor> = mutableStateListOf()
//    private val route: SnapshotStateList<MapPoint> = mutableStateListOf()
//    private val locations: SnapshotStateList<MapPoint> = mutableStateListOf()
//    private val orderStatus: MutableState<OrderStatus?> = mutableStateOf(null)
//
//    private lateinit var context: Context
//    private lateinit var coroutineScope: CoroutineScope
//    private lateinit var cameraState: ComposableCameraState
//
//    @Composable
//    override fun Map(
//        startingPoint: MapPoint?,
//        enabled: Boolean,
//        modifier: Modifier,
//        contentPadding: PaddingValues
//    ) {
//        context = LocalContext.current
//        coroutineScope = rememberCoroutineScope()
//        cameraState = rememberCameraState(CameraPosition(GeoPoint(0.0, 0.0), Zoom(2.0f)))
//        val cameraNode by cameraState.node.collectAsStateWithLifecycle()
//
//        LaunchedEffect(cameraState.position.point) {
//            launch(Dispatchers.Main) {
//                mapPoint.value = MapPoint(
//                    cameraState.position.point.lat,
//                    cameraState.position.point.lon
//                )
//            }
//        }
//
//        MapView(
//            modifier = modifier,
//            cameraState = cameraState
//        ) {
//            if (
//                orderStatus.value == OrderStatus.Appointed ||
//                orderStatus.value == OrderStatus.AtAddress
//            ) Marker(
//                icon = imageFromResource(R.drawable.ic_origin_marker),
//                position = GeoPoint(locations.first().lat, locations.first().lng)
//            )
//
//            Markers(
//                route = route,
//                locations = locations
//            )
//
//            Driver(driver)
//
//            Drivers(drivers)
//        }
//
//        DisposableEffect(cameraNode?.dgisCamera?.position) {
//            val closable = cameraNode?.stateChannel?.connect { state ->
//                isMarkerMoving.value = (state != CameraState.FREE)
//            }
//            onDispose { closable?.close() }
//        }
//    }
//
//    override fun move(to: MapPoint) {
//        if (::cameraState.isInitialized) coroutineScope.launch {
//            cameraState.move(
//                duration = Duration.ZERO,
//                position = CameraPosition(
//                    point = GeoPoint(to.lat, to.lng),
//                    zoom = Zoom(15f)
//                )
//            )
//        }
//    }
//
//    override fun animate(to: MapPoint, durationMillis: Int) {
//        if (::cameraState.isInitialized) coroutineScope.launch {
//            cameraState.move(
//                duration = Duration.ofMilliseconds(durationMillis.toLong()),
//                position = CameraPosition(
//                    point = GeoPoint(to.lat, to.lng),
//                    zoom = Zoom(15f)
//                )
//            )
//        }
//    }
//
//    override fun moveToMyLocation() {
//        if (::cameraState.isInitialized) getCurrentLocation(context) { location ->
//            val mapPoint = location.let { MapPoint(it.latitude, it.longitude) }
//            move(mapPoint)
//        }
//    }
//
//    override fun animateToMyLocation(durationMillis: Int) {
//        if (::cameraState.isInitialized) getCurrentLocation(context) { location ->
//            val mapPoint = MapPoint(location.latitude, location.longitude)
//            animate(mapPoint, durationMillis)
//        }
//    }
//
//    override fun moveToFitBounds(routing: List<MapPoint>) {
//        if (::cameraState.isInitialized) coroutineScope.launch {
//            cameraState.node.value?.dgisCamera?.let { camera ->
//                val list = routing.map { PointGeometry(GeoPoint(it.lat, it.lng)) }
//                val geometry = ComplexGeometry(list)
//                val position = calcPosition(camera, geometry)
//
//                cameraState.move(
//                    position = position,
//                    duration = Duration.ZERO
//                )
//            }
//        }
//    }
//
//    override fun animateToFitBounds(routing: List<MapPoint>) {
//        if (::cameraState.isInitialized) coroutineScope.launch {
//            cameraState.node.value?.dgisCamera?.let { camera ->
//                val list = routing.map { PointGeometry(GeoPoint(it.lat, it.lng)) }
//                val geometry = ComplexGeometry(list)
//                val position = calcPosition(camera, geometry)
//
//                cameraState.move(
//                    position = position,
//                    duration = Duration.ofMilliseconds(1000L)
//                )
//            }
//        }
//    }
//
//    override fun updateDriver(driver: ShowOrderModel.Executor) {
//        this.driver.value = driver.let { show ->
//            Executor(
//                id = show.id,
//                lat = show.coords.lat,
//                lng = show.coords.lng,
//                heading = show.coords.heading,
//                distance = 0.0
//            )
//        }
//    }
//
//    override fun updateDrivers(drivers: List<Executor>) {
//        this.drivers.clear()
//        this.drivers.addAll(drivers)
//    }
//
//    override fun updateRoute(route: List<MapPoint>) {
//        this.route.clear()
//        this.route.addAll(route)
//    }
//
//    override fun updateOrderStatus(status: OrderStatus) {
//        orderStatus.value = status
//    }
//
//    override fun updateLocations(locations: List<MapPoint>) {
//        this.locations.clear()
//        this.locations.addAll(locations)
//    }
//
//    override fun zoomOut() {
//        val currentZoomLevel = cameraState.position.zoom.value
//        if (::cameraState.isInitialized && currentZoomLevel > 15f) coroutineScope.launch {
//            cameraState.move(
//                duration = Duration.ofMilliseconds(1000L),
//                position = CameraPosition(
//                    point = cameraState.position.point,
//                    zoom = Zoom(cameraState.position.zoom.value - 1f)
//                )
//            )
//        }
//    }
//}
//
//@Composable
//private fun Driver(
//    driver: State<Executor?>
//) {
//    driver.value?.let {
//        Marker(
//            icon = imageFromResource(R.drawable.img_car_marker),
//            position = GeoPoint(it.lng, it.lng)
//        )
//    }
//}
//
//@Composable
//private fun Drivers(
//    drivers: SnapshotStateList<Executor>
//) {
//    drivers.take(20).forEach {
//        Marker(
//            icon = imageFromResource(R.drawable.img_car_marker),
//            position = GeoPoint(it.lat, it.lng)
//        )
//    }
//}
//
//@Composable
//private fun Markers(
//    route: List<MapPoint>,
//    locations: List<MapPoint>
//) {
//    if (route.isNotEmpty()) {
//        Polyline(
//            points = route.map { GeoPoint(it.lat, it.lng) },
//            width = 4.dp
//        )
//
//        Marker(
//            icon = imageFromResource(R.drawable.ic_origin_marker),
//            position = GeoPoint(route.first().lat, route.first().lng)
//        )
//
//        if (locations.size > 2) locations.dropLast(1).forEach {
//            Marker(
//                icon = imageFromResource(R.drawable.ic_middle_marker),
//                position = GeoPoint(it.lat, it.lng)
//            )
//        }
//
//        Marker(
//            icon = imageFromResource(R.drawable.ic_destination_marker),
//            position = GeoPoint(route.last().lat, route.last().lng)
//        )
//    }
//}