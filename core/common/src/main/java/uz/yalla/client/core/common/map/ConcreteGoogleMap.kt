package uz.yalla.client.core.common.map

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.convertor.dpToPx
import uz.yalla.client.core.common.marker.createInfoMarker
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.common.utils.hasLocationPermission
import uz.yalla.client.core.common.utils.vectorToBitmapDescriptor
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import kotlin.properties.Delegates

class ConcreteGoogleMap : MapStrategy, KoinComponent {
    private val prefs by inject<AppPreferences>()

    override val isMarkerMoving = MutableStateFlow(false to false)

    override val mapPoint: MutableState<MapPoint> = mutableStateOf(MapPoint(0.0, 0.0))

    private var driver: MutableState<Executor?> = mutableStateOf(null)
    private val drivers: SnapshotStateList<Executor> = mutableStateListOf()
    private val route: SnapshotStateList<MapPoint> = mutableStateListOf()
    private val locations: SnapshotStateList<MapPoint> = mutableStateListOf()
    private val orderStatus: MutableState<OrderStatus?> = mutableStateOf(null)
    private val carArrivesInMinutes: MutableState<Int?> = mutableStateOf(null)
    private val orderEndsInMinutes: MutableState<Int?> = mutableStateOf(null)

    private lateinit var context: Context
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var cameraPositionState: CameraPositionState
    private var mapPadding by Delegates.notNull<Int>()

    @Composable
    override fun Map(
        startingPoint: MapPoint?,
        enabled: Boolean,
        modifier: Modifier,
        contentPadding: PaddingValues,
        onMapReady: () -> Unit
    ) {
        context = LocalContext.current
        mapPadding = dpToPx(context, 100)
        coroutineScope = rememberCoroutineScope()
        cameraPositionState = rememberCameraPositionState()

        val savedLoc by prefs.entryLocation.collectAsState(initial = 0.0 to 0.0)

        LaunchedEffect(savedLoc) {
            mapPoint.value = MapPoint(savedLoc.first, savedLoc.second)
        }

        LaunchedEffect(::cameraPositionState.isInitialized) {
            launch(Dispatchers.Main) {
                awaitFrame()
                if (startingPoint != null) move(startingPoint)
                else move(MapPoint(savedLoc.first, savedLoc.second))
            }
        }

        LaunchedEffect(cameraPositionState) {
            snapshotFlow { cameraPositionState.isMoving }
                .collect { isMoving ->
                    isMarkerMoving.emit(
                        isMoving to
                                (cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE)
                    )
                    if (!isMoving) {
                        val target = cameraPositionState.position.target
                        mapPoint.value = MapPoint(target.latitude, target.longitude)
                    }
                }
        }

        val hasLocationPermission = context.hasLocationPermission()

        GoogleMap(
            mergeDescendants = true,
            modifier = modifier,
            cameraPositionState = cameraPositionState,
            contentPadding = contentPadding,
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = hasLocationPermission
            ),
            uiSettings = MapUiSettings(
                scrollGesturesEnabled = enabled,
                zoomGesturesEnabled = enabled,
                compassEnabled = false,
                mapToolbarEnabled = false,
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                tiltGesturesEnabled = false,
                scrollGesturesEnabledDuringRotateOrZoom = false
            ),
            onMapLoaded = {
                startingPoint?.let { move(to = it) }
                onMapReady()
            }
        ) {
            Markers(
                route = route,
                locations = locations,
                orderStatus = orderStatus.value,
                carArrivesInMinutes = carArrivesInMinutes.value.takeIf { orderStatus.value == null },
                orderEndsInMinutes = orderEndsInMinutes.value.takeIf { orderStatus.value == null }
            )

            Driver(driver = driver)

            Drivers(drivers = drivers)
        }
    }

    override fun move(to: MapPoint) {
        if (::cameraPositionState.isInitialized) {
            cameraPositionState.move(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(LatLng(to.lat, to.lng), 15f, 0.0f, 0.0f)
                )
            )
        }
    }

    override fun animate(to: MapPoint, durationMillis: Int) {
        if (::cameraPositionState.isInitialized) coroutineScope.launch {
            cameraPositionState.animate(
                durationMs = durationMillis,
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(LatLng(to.lat, to.lng), 15f, 0.0f, 0.0f)
                )
            )
        }
    }

    override fun moveToMyLocation() {
        if (::cameraPositionState.isInitialized) getCurrentLocation(context) { location ->
            val mapPoint = location.let { MapPoint(it.latitude, it.longitude) }
            move(mapPoint)
        }
    }

    override fun animateToMyLocation(durationMillis: Int) {
        if (::cameraPositionState.isInitialized) getCurrentLocation(context) { location ->
            val mapPoint = location.let { MapPoint(it.latitude, it.longitude) }
            animate(mapPoint, durationMillis)
        }
    }

    override fun moveToFitBounds(routing: List<MapPoint>) {
        if (::cameraPositionState.isInitialized && routing.isNotEmpty()) coroutineScope.launch {
            val boundsBuilder = LatLngBounds.Builder()
            routing.forEach { boundsBuilder.include(LatLng(it.lat, it.lng)) }
            val bounds = boundsBuilder.build()
            cameraPositionState.move(
                update = CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    mapPadding
                )
            )
        }
    }

    override fun animateToFitBounds(routing: List<MapPoint>) {
        if (::cameraPositionState.isInitialized && routing.isNotEmpty()) coroutineScope.launch {
            val boundsBuilder = LatLngBounds.Builder()
            routing.forEach { boundsBuilder.include(LatLng(it.lat, it.lng)) }
            val bounds = boundsBuilder.build()
            cameraPositionState.animate(
                durationMs = 1000,
                update = CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    mapPadding
                )
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

    override fun zoomOut() {
        if (::cameraPositionState.isInitialized) {
            val currentPosition = cameraPositionState.position
            val currentTarget = currentPosition.target

            if (currentPosition.zoom > 14) {
                val newCameraPosition = CameraPosition.Builder()
                    .target(currentTarget)
                    .zoom(currentPosition.zoom.minus(0.2).toFloat())
                    .bearing(currentPosition.bearing)
                    .tilt(currentPosition.tilt)
                    .build()

                coroutineScope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newCameraPosition(newCameraPosition),
                        durationMs = 1000
                    )
                }
            }
        }
    }
}

@Composable
private fun Driver(
    driver: State<Executor?>
) {
    driver.value?.let {
        MarkerComposable(
            flat = true,
            rotation = it.heading.toFloat(),
            state = rememberMarkerState(position = LatLng(it.lat, it.lng))
        ) {
            Icon(
                painter = painterResource(R.drawable.img_car_marker),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
private fun Drivers(
    drivers: SnapshotStateList<Executor>
) {
    drivers.take(20).forEach { driver ->
        MarkerComposable(
            flat = true,
            rotation = driver.heading.toFloat(),
            state = remember(driver) { MarkerState(LatLng(driver.lat, driver.lng)) }
        ) {
            Icon(
                painter = painterResource(R.drawable.img_car_marker),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(48.dp)
            )
        }
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
    val context = LocalContext.current

    val originIcon = key(
        locations.firstOrNull()?.hashCode()?.plus(carArrivesInMinutes.hashCode()),
    ) {
        createInfoMarker(
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

    val middleIcon = remember {
        vectorToBitmapDescriptor(context, R.drawable.ic_middle_marker)
            ?: BitmapDescriptorFactory.defaultMarker()
    }

    val endIcon = key(
        locations.lastOrNull()?.hashCode()?.plus(orderEndsInMinutes.hashCode())
    ) {
        createInfoMarker(
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
        Polyline(points = route.map { LatLng(it.lat, it.lng) })
    }

    if (locations.isEmpty()) return

    if (orderStatus != null || route.isNotEmpty()) {
        val start = locations.first()
        key(start.hashCode()) {
            Marker(
                state = rememberMarkerState(position = LatLng(start.lat, start.lng)),
                icon = originIcon
            )
        }
    }

    if (locations.size > 2) {
        locations.subList(1, locations.lastIndex).forEachIndexed { index, mid ->
            key(mid.hashCode(), index) {
                Marker(
                    state = rememberMarkerState(position = LatLng(mid.lat, mid.lng)),
                    icon = middleIcon
                )
            }
        }
    }

    if (locations.size > 1) {
        val end = locations.last()
        key(end.hashCode()) {
            Marker(
                state = rememberMarkerState(position = LatLng(end.lat, end.lng)),
                icon = endIcon
            )
        }
    }
}