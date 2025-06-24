package uz.yalla.client.core.common.map

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.sargunv.maplibrecompose.compose.CameraState
import dev.sargunv.maplibrecompose.compose.MaplibreMap
import dev.sargunv.maplibrecompose.compose.StyleState
import dev.sargunv.maplibrecompose.compose.layer.LineLayer
import dev.sargunv.maplibrecompose.compose.layer.SymbolLayer
import dev.sargunv.maplibrecompose.compose.rememberCameraState
import dev.sargunv.maplibrecompose.compose.rememberStyleState
import dev.sargunv.maplibrecompose.compose.source.rememberGeoJsonSource
import dev.sargunv.maplibrecompose.core.CameraMoveReason
import dev.sargunv.maplibrecompose.core.CameraPosition
import dev.sargunv.maplibrecompose.core.GestureSettings
import dev.sargunv.maplibrecompose.core.OrnamentSettings
import dev.sargunv.maplibrecompose.expressions.dsl.const
import dev.sargunv.maplibrecompose.expressions.dsl.image
import dev.sargunv.maplibrecompose.expressions.value.LineCap
import dev.sargunv.maplibrecompose.expressions.value.LineJoin
import io.github.dellisd.spatialk.geojson.BoundingBox
import io.github.dellisd.spatialk.geojson.Feature
import io.github.dellisd.spatialk.geojson.FeatureCollection
import io.github.dellisd.spatialk.geojson.LineString
import io.github.dellisd.spatialk.geojson.Point
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.marker.rememberLibreMarkerWithInfo
import uz.yalla.client.core.common.utils.findClosestPointOnRoute
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import kotlin.time.Duration.Companion.milliseconds

class ConcreteLibreMap : MapStrategy, KoinComponent {
    private val prefs by inject<AppPreferences>()

    override val isMarkerMoving = MutableStateFlow(Triple(false, false, MapPoint(0.0, 0.0)))
    override val mapPoint: MutableState<MapPoint> = mutableStateOf(MapPoint(0.0, 0.0))

    private var padding = mutableStateOf(PaddingValues())
    private var driver: MutableState<Executor?> = mutableStateOf(null)
    private val drivers: SnapshotStateList<Executor> = mutableStateListOf()
    private val route: SnapshotStateList<MapPoint> = mutableStateListOf()
    private val locations: SnapshotStateList<MapPoint> = mutableStateListOf()
    private val orderStatus: MutableState<OrderStatus?> = mutableStateOf(null)
    private val carArrivesInMinutes: MutableState<Int?> = mutableStateOf(null)
    private val orderEndsInMinutes: MutableState<Int?> = mutableStateOf(null)

    private lateinit var context: Context
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var cameraState: CameraState
    private lateinit var styleState: StyleState

    @Composable
    override fun Map(
        startingPoint: MapPoint?,
        enabled: Boolean,
        modifier: Modifier,
        contentPadding: PaddingValues,
        onMapReady: () -> Unit
    ) {
        context = LocalContext.current
        padding.value = contentPadding
        coroutineScope = rememberCoroutineScope()
        styleState = rememberStyleState()

        val savedLoc by prefs.entryLocation.collectAsState(initial = 0.0 to 0.0)

        // Initialize map point from saved location immediately
        LaunchedEffect(savedLoc) {
            if (savedLoc.first != 0.0 && savedLoc.second != 0.0) {
                mapPoint.value = MapPoint(savedLoc.first, savedLoc.second)
            }
        }

        cameraState = rememberCameraState(
            firstPosition = CameraPosition(
                zoom = 15.0,
                target = Position(
                    latitude = mapPoint.value.lat,
                    longitude = mapPoint.value.lng
                )
            )
        )

        val mapReadyCalled = remember { mutableStateOf(false) }

        // Separate initialization logic
        LaunchedEffect(cameraState, savedLoc) {
            if (savedLoc.first == 0.0 && savedLoc.second == 0.0) return@LaunchedEffect
            coroutineScope {
                try {
                    cameraState.awaitInitialized()

                    // Small delay to ensure map is fully ready (similar to Google Maps onMapLoaded)
                    delay(100)

                    // Update map point from saved location
                    mapPoint.value = MapPoint(savedLoc.first, savedLoc.second)

                    // Move to starting point or saved location using immediate positioning
                    val targetPoint = startingPoint ?: MapPoint(savedLoc.first, savedLoc.second)

                    // Use moveTo for immediate positioning without animation during initialization
                    cameraState.animateTo(
                        CameraPosition(
                            zoom = 15.0,
                            target = Position(
                                latitude = targetPoint.lat,
                                longitude = targetPoint.lng
                            )
                        )
                    )

                    onMapReady()
                } catch (e: Exception) {
                    Log.e("ConcreteLibreMap", "Error initializing map", e)
                    onMapReady()
                }
            }
        }

        // Camera movement tracking (keep separate from initialization)
        LaunchedEffect(cameraState) {
            snapshotFlow { cameraState.isCameraMoving }
                .collect { isMoving ->
                    isMarkerMoving.emit(
                        Triple(
                            isMoving,
                            cameraState.moveReason == CameraMoveReason.GESTURE,
                            mapPoint.value
                        )
                    )
                    if (!isMoving) {
                        val target = cameraState.position.target
                        mapPoint.value = MapPoint(target.latitude, target.longitude)
                        Log.d("ConcreteLibreMap", "Camera moved to: ${mapPoint.value}")
                    }
                }
        }

        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            styleUri = "https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json",
            cameraState = cameraState,
            styleState = styleState, // Use the class property, not rememberStyleState()
            ornamentSettings = OrnamentSettings(
                padding = contentPadding,
                isScaleBarEnabled = false,
                isLogoEnabled = false,
                isCompassEnabled = false,
                isAttributionEnabled = false
            ),
            gestureSettings = GestureSettings(
                isTiltGesturesEnabled = false,
                isKeyboardGesturesEnabled = false
            )
        ) {
            Markers(
                route = route,
                locations = locations,
                orderStatus = orderStatus.value,
                carArrivesInMinutes = carArrivesInMinutes.value.takeIf { orderStatus.value == null },
                orderEndsInMinutes = orderEndsInMinutes.value.takeIf { orderStatus.value == null }
            )
        }
    }

    override fun move(to: MapPoint) {
        coroutineScope.launch {
            cameraState.animateTo(
                duration = 1.milliseconds,
                finalPosition = CameraPosition(
                    zoom = 15.0,
                    padding = padding.value,
                    target = Position(
                        latitude = to.lat,
                        longitude = to.lng
                    )
                )
            )
        }
    }

    override fun moveWithoutZoom(to: MapPoint) {
        coroutineScope.launch {
            cameraState.animateTo(
                duration = 1.milliseconds,
                finalPosition = CameraPosition(
                    padding = padding.value,
                    target = Position(
                        latitude = to.lat,
                        longitude = to.lng
                    )
                )
            )
        }
    }

    override fun animate(to: MapPoint, durationMillis: Int) {
        coroutineScope.launch {
            cameraState.animateTo(
                duration = durationMillis.milliseconds,
                finalPosition = CameraPosition(
                    zoom = 15.0,
                    padding = padding.value,
                    target = Position(
                        latitude = to.lat,
                        longitude = to.lng
                    )
                )
            )
        }
    }

    override fun zoomOut() {
        if (cameraState.position.zoom > 14) {
            coroutineScope.launch {
                cameraState.animateTo(
                    duration = 1.milliseconds,
                    finalPosition = CameraPosition(
                        zoom = cameraState.position.zoom - 0.2,
                        target = cameraState.position.target
                    )
                )
            }
        }
    }

    override fun moveToMyLocation() {
        coroutineScope.launch {
            cameraState.awaitInitialized()
            getCurrentLocation(context) { location ->
                val mapPoint = location.let { MapPoint(it.latitude, it.longitude) }
                move(mapPoint)
            }
        }
    }

    override fun animateToMyLocation(durationMillis: Int) {
        coroutineScope.launch {
            cameraState.awaitInitialized()
            getCurrentLocation(context) { location ->
                val mapPoint = location.let { MapPoint(it.latitude, it.longitude) }
                animate(mapPoint, durationMillis)
            }
        }
    }

    override fun moveToFitBounds(routing: List<MapPoint>) {
        coroutineScope.launch {
            cameraState.awaitInitialized()
            cameraState.animateTo(
                duration = 1.milliseconds,
                boundingBox = routing.convertToBouncingBox(),
                padding = PaddingValues(100.dp)
            )
        }
    }

    override fun animateToFitBounds(routing: List<MapPoint>) {
        coroutineScope.launch {
            cameraState.awaitInitialized()

            cameraState.animateTo(
                boundingBox = routing.convertToBouncingBox(),
                padding = padding.value + PaddingValues(100.dp)
            )
        }
    }

    override fun updateDriver(driver: ShowOrderModel.Executor) {
        this.driver.value = driver.let { show ->
            Executor(
                id = show.id,
                lat = show.coords.lat,
                lng = show.coords.lng,
                heading = show.coords.heading,
                distance = 0.0
            )
        }
    }

    override fun updateDrivers(drivers: List<Executor>) {
        this.drivers.clear()
        this.drivers.addAll(drivers)
    }

    override fun updateRoute(route: List<MapPoint>) {
        this.route.clear()
        this.route.addAll(route)
    }

    override fun updateOrderStatus(status: OrderStatus?) {
        orderStatus.value = status
    }

    override fun updateLocations(locations: List<MapPoint>) {
        this.locations.clear()
        this.locations.addAll(locations)
    }

    override fun updateCarArrivesInMinutes(carArrivesInMinutes: Int?) {
        this.carArrivesInMinutes.value = carArrivesInMinutes
    }

    override fun updateOrderEndsInMinutes(orderEndsInMinutes: Int?) {
        this.orderEndsInMinutes.value = orderEndsInMinutes
    }
}

@Composable
private fun Markers(
    route: List<MapPoint>,
    orderStatus: OrderStatus?,
    locations: List<MapPoint>,
    carArrivesInMinutes: Int? = null,
    orderEndsInMinutes: Int? = null
) {
    val originIcon = key(
        locations.firstOrNull()?.hashCode()?.plus(carArrivesInMinutes.hashCode())
    ) {
        rememberLibreMarkerWithInfo(
            key = "origin_${locations.firstOrNull()?.hashCode()}",
            title = carArrivesInMinutes?.let {
                stringResource(
                    R.string.x_min,
                    it.toString()
                )
            },
            description = stringResource(R.string.coming),
            infoColor = YallaTheme.color.primary,
            pointColor = YallaTheme.color.gray
        )
    }

    val middleIcon = painterResource(R.drawable.ic_middle_marker)

    val endIcon = key(
        locations.lastOrNull()?.hashCode()?.plus(orderEndsInMinutes.hashCode())
    ) {
        rememberLibreMarkerWithInfo(
            key = "destination_${locations.lastOrNull()?.hashCode()}",
            title = orderEndsInMinutes?.let {
                stringResource(
                    R.string.x_min,
                    it.toString()
                )
            },
            description = stringResource(R.string.on_the_way),
            infoColor = YallaTheme.color.black,
            pointColor = YallaTheme.color.primary
        )
    }

    if (route.isNotEmpty()) {
        PolylineLayer(id = "line", coordinates = route)
    }

    if (route.isNotEmpty() && orderStatus !in OrderStatus.cancellable) {
        locations.forEachIndexed { index, location ->
            val target: MapPoint? = when (index) {
                0 -> route.firstOrNull()
                locations.lastIndex -> route.lastOrNull()
                else -> findClosestPointOnRoute(location, route)
            }

            if (target != null && target != location) {
                PolylineLayer(
                    id = "connection_$index",
                    coordinates = listOf(location, target),
                    color = YallaTheme.color.gray,
                    widthDp = 2f
                )
            }
        }
    }


    if (locations.isEmpty()) return

    if (orderStatus != null || route.isNotEmpty()) {
        val start = locations.first()
        key(start.hashCode()) {
            originIcon?.let { icon ->
                MarkerLayer(
                    id = "start",
                    point = start,
                    painter = icon
                )
            }
        }
    }

    if (locations.size > 2) {
        locations.subList(1, locations.lastIndex).forEachIndexed { index, mid ->
            key(mid.hashCode(), index) {
                MarkerLayer(
                    id = "middle_$index",
                    point = mid,
                    painter = middleIcon
                )
            }
        }
    }

    if (locations.size > 1) {
        val end = locations.last()
        key(end.hashCode()) {
            endIcon?.let { icon ->
                MarkerLayer(
                    id = "end",
                    point = end,
                    painter = icon
                )
            }
        }
    }
}

@Composable
private fun PolylineLayer(
    id: String,
    coordinates: List<MapPoint>,
    color: Color = Color.Black,
    widthDp: Float = 4f,
) {
    val geoJson = FeatureCollection(
        Feature(LineString(coordinates.map { Position(latitude = it.lat, longitude = it.lng) }))
    )
    val source = rememberGeoJsonSource(id = "$id-source", data = geoJson)

    LineLayer(
        id = id,
        source = source,
        color = const(color),
        width = const(widthDp.dp),
        cap = const(LineCap.Round),
        join = const(LineJoin.Round)
    )
}

@Composable
private fun MarkerLayer(
    id: String,
    point: MapPoint,
    painter: Painter
) {
    val geoJsonSource = rememberGeoJsonSource(
        id = id,
        data = Feature(Point(Position(latitude = point.lat, longitude = point.lng)))
    )

    SymbolLayer(
        id = id,
        source = geoJsonSource,
        iconImage = image(painter),
        iconAllowOverlap = const(true),
        iconIgnorePlacement = const(true)
    )
}

private infix operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    return PaddingValues(
        top = calculateTopPadding()
                + other.calculateTopPadding(),
        bottom = calculateBottomPadding()
                + other.calculateBottomPadding(),
        start = calculateLeftPadding(LayoutDirection.Ltr)
                + other.calculateLeftPadding(LayoutDirection.Ltr),
        end = calculateRightPadding(LayoutDirection.Ltr)
                + other.calculateRightPadding(LayoutDirection.Ltr)
    )
}

private fun List<MapPoint>.convertToBouncingBox(): BoundingBox {
    val latitudes = this.map { it.lat }
    val longitudes = this.map { it.lng }

    val minLat = latitudes.minOrNull() ?: return BoundingBox(emptyList())
    val maxLat = latitudes.maxOrNull() ?: return BoundingBox(emptyList())
    val minLng = longitudes.minOrNull() ?: return BoundingBox(emptyList())
    val maxLng = longitudes.maxOrNull() ?: return BoundingBox(emptyList())

    return BoundingBox(listOf(minLng, minLat, maxLng, maxLat))
}