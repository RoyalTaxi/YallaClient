package uz.yalla.client.core.common.map

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.convertor.dpToPx
import uz.yalla.client.core.common.marker.rememberGoogleMarkerWithInfo
import uz.yalla.client.core.common.utils.findClosestPointOnRoute
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.common.utils.hasLocationPermission
import uz.yalla.client.core.common.utils.vectorToBitmapDescriptor
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.domain.model.type.ThemeType
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.properties.Delegates

private fun loadMapStyleFromRaw(context: Context): String {
    return try {
        val inputStream = context.resources.openRawResource(R.raw.google_dark_map)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
        reader.close()
        stringBuilder.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

class ConcreteGoogleMap : MapStrategy, KoinComponent {
    override val isMarkerMoving = MutableStateFlow(Triple(false, false, MapPoint(0.0, 0.0)))
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
    private val prefs by inject<AppPreferences>()
    private var themeType by mutableStateOf<ThemeType?>(null)

    @OptIn(FlowPreview::class)
    @Composable
    override fun Map(
        startingPoint: MapPoint?,
        enabled: Boolean,
        modifier: Modifier,
        contentPadding: PaddingValues,
        isMyLocationEnabled: Boolean,
        onMapReady: () -> Unit
    ) {
        context = LocalContext.current
        mapPadding = dpToPx(context, 110)
        coroutineScope = rememberCoroutineScope()
        cameraPositionState = rememberCameraPositionState()
        val isSystemInDarkTheme = isSystemInDarkTheme()

        LaunchedEffect(Unit) {
            prefs.themeType.collectLatest {
                themeType = when (it) {
                    ThemeType.SYSTEM -> if (isSystemInDarkTheme) ThemeType.DARK else ThemeType.LIGHT
                    else -> it
                }
            }
        }

        LaunchedEffect(cameraPositionState) {
            awaitFrame()
            startingPoint?.let {
                move(it)
                mapPoint.value = it
            } ?: run {
                moveToMyLocation()
            }
        }

        val driversVisibility by remember(cameraPositionState.position.zoom) {
            mutableStateOf(cameraPositionState.position.zoom >= 8)
        }

        LaunchedEffect(cameraPositionState) {
            snapshotFlow { cameraPositionState.isMoving }
                .distinctUntilChanged()
                .debounce(50)
                .collect { isMoving ->
                    val isGesture =
                        cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE

                    if (!isMoving) {
                        val target = cameraPositionState.position.target
                        mapPoint.value = MapPoint(target.latitude, target.longitude)
                    }

                    isMarkerMoving.emit(Triple(isMoving, isGesture, mapPoint.value))
                }
        }

        key(themeType) {
            GoogleMap(
                mergeDescendants = true,
                modifier = modifier,
                cameraPositionState = cameraPositionState,
                contentPadding = contentPadding,
                properties = MapProperties(
                    mapStyleOptions = if (themeType == ThemeType.DARK) MapStyleOptions(
                        loadMapStyleFromRaw(context)
                    ) else null,
                    mapType = MapType.NORMAL,
                    isMyLocationEnabled = isMyLocationEnabled && context.hasLocationPermission()
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
                    startingPoint?.let(::move)
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

                if (driversVisibility) {
                    Driver(driver = driver)

                    DriversWithAnimation(drivers = drivers)
                }
            }
        }
    }

    override fun move(to: MapPoint) {
        if (::cameraPositionState.isInitialized) {
            mapPoint.value = to
            cameraPositionState.move(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(LatLng(to.lat, to.lng), 15f, 0.0f, 0.0f)
                )
            )
        }
    }

    override fun moveWithoutZoom(to: MapPoint) {
        if (::cameraPositionState.isInitialized) {
            mapPoint.value = to
            cameraPositionState.move(
                update = CameraUpdateFactory.newLatLng(
                    LatLng(to.lat, to.lng)
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
            val mapPoint = MapPoint(location.latitude, location.longitude)
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
        if (::cameraPositionState.isInitialized && routing.isNotEmpty()) {
            updateRoute(routing)

            coroutineScope.launch {
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
            state = rememberMarkerState(position = LatLng(it.lat, it.lng)),
            anchor = Offset(0.5f, 0.5f),
            zIndex = 1f
        ) {
            Icon(
                painter = painterResource(R.drawable.img_car_marker),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun DriversWithAnimation(
    drivers: SnapshotStateList<Executor>
) {
    drivers.take(20).forEach { driver ->
        key(driver.id) {
            val markerState = rememberMarkerState(key = driver.id.toString())

            val animatedLat = remember { Animatable(driver.lat.toFloat()) }
            val animatedLng = remember { Animatable(driver.lng.toFloat()) }
            val animatedHeading = remember { Animatable(driver.heading.toFloat()) }

            // Track animation state to prevent overlapping animations
            val isAnimating = remember { mutableStateOf(false) }

            LaunchedEffect(driver.lat, driver.lng, driver.heading) {
                if (!isAnimating.value) {
                    isAnimating.value = true

                    // Calculate distance for dynamic duration
                    val distance = calculateDistance(
                        animatedLat.value.toDouble(),
                        animatedLng.value.toDouble(),
                        driver.lat,
                        driver.lng
                    )

                    val duration = when {
                        distance < 0.0005 -> 300 // Very close
                        distance < 0.002 -> 800  // Close
                        distance < 0.01 -> 1200  // Medium
                        else -> 1800             // Far
                    }

                    // Animate all properties simultaneously
                    launch {
                        animatedLat.animateTo(
                            targetValue = driver.lat.toFloat(),
                            animationSpec = tween(duration, easing = FastOutSlowInEasing)
                        )
                    }

                    launch {
                        animatedLng.animateTo(
                            targetValue = driver.lng.toFloat(),
                            animationSpec = tween(duration, easing = FastOutSlowInEasing)
                        )
                    }

                    launch {
                        val targetHeading = normalizeHeading(driver.heading.toFloat())
                        animatedHeading.animateTo(
                            targetValue = targetHeading,
                            animationSpec = tween(
                                (duration * 1.1).toInt(),
                                easing = FastOutSlowInEasing
                            )
                        )
                    }

                    isAnimating.value = false
                }
            }

            markerState.position =
                LatLng(animatedLat.value.toDouble(), animatedLng.value.toDouble())

            MarkerComposable(
                state = markerState,
                rotation = animatedHeading.value,
                flat = true,
                anchor = Offset(0.5f, 0.5f),
                zIndex = 1f
            ) {
                Icon(
                    painter = painterResource(R.drawable.img_car_marker),
                    contentDescription = "Driver",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }
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
        rememberGoogleMarkerWithInfo(
            key = "origin_${locations.firstOrNull()?.hashCode()}",
            title = carArrivesInMinutes?.let {
                stringResource(
                    R.string.x_min,
                    it.toString()
                )
            },
            description = stringResource(R.string.coming),
            infoColor = YallaTheme.color.primary,
            pointColor = YallaTheme.color.gray,
            pointBackgroundColor = YallaTheme.color.background
        )
    }

    val middleIcon = remember {
        vectorToBitmapDescriptor(context, R.drawable.ic_middle_marker)
            ?: BitmapDescriptorFactory.defaultMarker()
    }

    val endIcon = key(
        locations.lastOrNull()?.hashCode()?.plus(orderEndsInMinutes.hashCode())
    ) {
        rememberGoogleMarkerWithInfo(
            key = "destination_${locations.lastOrNull()?.hashCode()}",
            title = orderEndsInMinutes?.let {
                stringResource(
                    R.string.x_min,
                    it.toString()
                )
            },
            description = stringResource(R.string.on_the_way),
            infoColor = YallaTheme.color.black,
            pointColor = YallaTheme.color.primary,
            pointBackgroundColor = YallaTheme.color.background
        )
    }

    if (route.isNotEmpty()) {
        val isDarkTheme = isSystemInDarkTheme()
        Polyline(
            points = route.map { LatLng(it.lat, it.lng) },
            color = if (isDarkTheme) Color.White else Color.Black
        )
    }

    if (route.isNotEmpty() && orderStatus !in OrderStatus.cancellable) {
        locations.forEachIndexed { index, location ->
            val target: MapPoint? = when (index) {
                0 -> route.firstOrNull()
                locations.lastIndex -> route.lastOrNull()
                else -> findClosestPointOnRoute(location, route)
            }

            if (target != null && target != location) {
                Polyline(
                    points = listOf(
                        LatLng(location.lat, location.lng),
                        LatLng(target.lat, target.lng)
                    ),
                    color = YallaTheme.color.gray,
                    pattern = listOf(Dash(16f), Gap(16f))
                )
            }
        }
    }

    if (locations.isEmpty()) return

    if ((orderStatus != null && orderStatus in OrderStatus.nonInteractive) || route.isNotEmpty()) {
        val start = locations.first()
        key(start.hashCode()) {
            Marker(
                state = rememberMarkerState(position = LatLng(start.lat, start.lng)),
                icon = originIcon,
                zIndex = 2f
            )
        }
    }

    if (locations.size > 2) {
        locations.subList(1, locations.lastIndex).forEachIndexed { index, mid ->
            key(mid.hashCode(), index) {
                Marker(
                    state = rememberMarkerState(position = LatLng(mid.lat, mid.lng)),
                    icon = middleIcon,
                    zIndex = 2f
                )
            }
        }
    }

    if (locations.size > 1) {
        val end = locations.last()
        key(end.hashCode()) {
            Marker(
                state = rememberMarkerState(position = LatLng(end.lat, end.lng)),
                icon = endIcon,
                zIndex = 2f // INFO MARKERS SHOULD BE ON TOP
            )
        }
    }
}

private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) *
            cos(Math.toRadians(lat2)) *
            sin(dLng / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadiusKm * c
}

private fun normalizeHeading(heading: Float): Float {
    var normalized = heading % 360
    if (normalized < 0) normalized += 360
    return normalized
}
