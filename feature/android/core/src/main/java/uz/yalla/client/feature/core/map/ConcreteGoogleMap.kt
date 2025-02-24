package uz.yalla.client.feature.core.map

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
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
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android2gis.dpToPx
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.ildam.technologies.yalla.core.domain.model.Executor
import uz.ildam.technologies.yalla.core.domain.model.MapPoint
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderStatus
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.core.R
import uz.yalla.client.feature.core.utils.getCurrentLocation
import uz.yalla.client.feature.core.utils.vectorToBitmapDescriptor
import kotlin.properties.Delegates

class ConcreteGoogleMap : MapStrategy {
    override val isMarkerMoving: MutableState<Boolean> = mutableStateOf(false)
    override val mapPoint: MutableState<MapPoint> = mutableStateOf(
        MapPoint(
            lat = AppPreferences.entryLocation.first,
            lng = AppPreferences.entryLocation.second
        )
    )

    private var driver: MutableState<Executor?> = mutableStateOf(null)
    private val drivers: SnapshotStateList<Executor> = mutableStateListOf()
    private val route: SnapshotStateList<MapPoint> = mutableStateListOf()
    private val locations: SnapshotStateList<MapPoint> = mutableStateListOf()
    private val orderStatus: MutableState<OrderStatus?> = mutableStateOf(null)

    private lateinit var context: Context
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var cameraPositionState: CameraPositionState

    private var mapPadding by Delegates.notNull<Int>()

    @Composable
    override fun Map(
        startingPoint: MapPoint?,
        modifier: Modifier,
        contentPadding: PaddingValues
    ) {
        context = LocalContext.current
        mapPadding = dpToPx(context, 100)
        coroutineScope = rememberCoroutineScope()
        cameraPositionState = rememberCameraPositionState()

        LaunchedEffect(cameraPositionState) {
            awaitFrame()
            startingPoint?.let { mapPoint.value = it }
        }

        LaunchedEffect(cameraPositionState.isMoving) {
            isMarkerMoving.value = cameraPositionState.isMoving
        }

        LaunchedEffect(cameraPositionState.position) {
            mapPoint.value = MapPoint(
                cameraPositionState.position.target.latitude,
                cameraPositionState.position.target.longitude
            )
        }

        GoogleMap(
            mergeDescendants = true,
            modifier = modifier,
            cameraPositionState = cameraPositionState,
            contentPadding = contentPadding,
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = true
            ),
            uiSettings = MapUiSettings(
                scrollGesturesEnabled = true,
                compassEnabled = false,
                mapToolbarEnabled = false,
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                tiltGesturesEnabled = false,
                scrollGesturesEnabledDuringRotateOrZoom = false
            )
        ) {
            if (
                orderStatus.value == OrderStatus.Appointed ||
                orderStatus.value == OrderStatus.AtAddress
            ) Marker(
                icon = remember {
                    vectorToBitmapDescriptor(context, R.drawable.ic_origin_marker)
                        ?: BitmapDescriptorFactory.defaultMarker()
                },
                state = remember(locations.firstOrNull()) {
                    MarkerState(
                        LatLng(
                            locations.firstOrNull()?.lat.or0(),
                            locations.firstOrNull()?.lng.or0(),
                        )
                    )
                }
            )

            Markers(route = route, locations = locations)

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

    override fun updateOrderStatus(status: OrderStatus) {
        orderStatus.value = status
    }

    override fun updateLocations(locations: List<MapPoint>) {
        this.locations.clear()
        this.locations.addAll(locations)
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
    locations: List<MapPoint>
) {
    val context = LocalContext.current
    val startMarkerIcon = remember {
        vectorToBitmapDescriptor(context, R.drawable.ic_origin_marker)
            ?: BitmapDescriptorFactory.defaultMarker()
    }
    val middleMarkerIcon = remember {
        vectorToBitmapDescriptor(context, R.drawable.ic_middle_marker)
            ?: BitmapDescriptorFactory.defaultMarker()
    }
    val endMarkerIcon = remember {
        vectorToBitmapDescriptor(context, R.drawable.ic_destination_marker)
            ?: BitmapDescriptorFactory.defaultMarker()
    }

    if (route.isNotEmpty()) {
        Polyline(points = route.map { LatLng(it.lat, it.lng) })

        Marker(
            icon = startMarkerIcon,
            state = remember(route.first()) {
                MarkerState(LatLng(route.first().lat, route.first().lng))
            }
        )

        if (locations.size > 2) for (dest in 1 until locations.lastIndex) {
            Marker(
                icon = middleMarkerIcon,
                state = remember(locations[dest]) {
                    MarkerState(
                        LatLng(
                            locations[dest].lat,
                            locations[dest].lng
                        )
                    )
                }
            )
        }

        Marker(
            icon = endMarkerIcon,
            state = remember(route.last()) {
                MarkerState(LatLng(route.last().lat, route.last().lng))
            }
        )
    }
}