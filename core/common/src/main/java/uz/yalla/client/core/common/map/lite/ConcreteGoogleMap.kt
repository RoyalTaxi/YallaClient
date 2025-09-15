package uz.yalla.client.core.common.map.lite

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
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
import uz.yalla.client.core.common.map.extended.google.GoogleDriver
import uz.yalla.client.core.common.map.extended.google.GoogleDriversWithAnimation
import uz.yalla.client.core.common.map.extended.google.GoogleMarkers
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.common.utils.hasLocationPermission
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.domain.model.type.ThemeType
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import java.io.BufferedReader
import java.io.InputStreamReader
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
                GoogleMarkers(
                    route = route,
                    locations = locations,
                    orderStatus = orderStatus.value,
                    carArrivesInMinutes = carArrivesInMinutes.value.takeIf { orderStatus.value == null },
                    orderEndsInMinutes = orderEndsInMinutes.value.takeIf { orderStatus.value == null }
                )

                if (driversVisibility) {
                    GoogleDriver(driver = driver.value)

                    GoogleDriversWithAnimation(drivers = drivers)
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