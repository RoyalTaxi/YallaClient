package uz.yalla.client.core.common.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.utils.createInfoMarkerBitmapDescriptor
import uz.yalla.client.core.common.utils.dpToPx
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.common.utils.vectorToBitmapDescriptor
import uz.yalla.client.core.common.utils.vectorToBitmapDescriptorWithSize
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.domain.model.type.ThemeType

data class MapsState(
    val isMapReady: Boolean = false,
    val savedCameraPosition: MapPoint? = null,
    val mapPaddingPx: Int = 0,
    val topPaddingPx: Int = 0,
    val bottomPaddingPx: Int = 0,
    val googleMarkPaddingPx: Int = 0,
    val route: List<MapPoint> = emptyList(),
    val locations: List<MapPoint> = emptyList(),
    val carArrivesInMinutes: Int? = null,
    val orderEndsInMinutes: Int? = null,
    val orderStatus: OrderStatus? = null,
    val driver: Executor? = null,
    val drivers: List<Executor> = emptyList()
)

data class CameraState(
    val position: MapPoint = MapPoint(0.0, 0.0),
    val isMoving: Boolean = false,
    val isByUser: Boolean = false
)

sealed interface MapsIntent {
    data class OnMapReady(val googleMap: GoogleMap) : MapsIntent
    data class MoveTo(val point: MapPoint) : MapsIntent
    data class AnimateTo(val point: MapPoint) : MapsIntent
    data class MoveToMyLocation(val context: Context) : MapsIntent
    data class AnimateToMyLocation(val context: Context) : MapsIntent
    data object FitBounds : MapsIntent
    data object AnimateFitBounds : MapsIntent
    data object ZoomOut : MapsIntent
    data class SetGoogleMarkPadding(val paddingPx: Int) : MapsIntent
    data class SetBottomPadding(val padding: Dp) : MapsIntent
    data class SetTopPadding(val padding: Dp) : MapsIntent
    data class SetMapPadding(val paddingPx: Int) : MapsIntent
    data class UpdateRoute(val route: List<MapPoint>) : MapsIntent
    data class UpdateLocations(val locations: List<MapPoint>) : MapsIntent
    data class UpdateCarArrivesInMinutes(val minutes: Int?) : MapsIntent
    data class UpdateOrderEndsInMinutes(val minutes: Int?) : MapsIntent
    data class UpdateOrderStatus(val status: OrderStatus?) : MapsIntent
    data class UpdateDriver(val driver: Executor?) : MapsIntent
    data class UpdateDrivers(val drivers: List<Executor>) : MapsIntent
}

class MapsViewModel(
    private val appContext: Context,
    private val appPreferences: AppPreferences
) : BaseViewModel(), LifeCycleAware, ContainerHost<MapsState, Nothing> {

    override val container: Container<MapsState, Nothing> = container(MapsState())

    private var map: GoogleMap? = null
    private val _cameraState = MutableStateFlow(CameraState())
    val cameraState = _cameraState.asStateFlow()

    private val jobs = mutableListOf<Job>()

    private var polyline: Polyline? = null
    private val dashedPolylines = mutableListOf<Polyline>()
    private val markers = mutableListOf<Marker>()
    private val driverMarkers = mutableListOf<Marker>()
    private val driversMarkers = mutableListOf<Marker>()

    private var originIcon: BitmapDescriptor? = null
    private var middleIcon: BitmapDescriptor? = null
    private var destinationIcon: BitmapDescriptor? = null
    private var originInfoIcon: BitmapDescriptor? = null
    private var destinationInfoIcon: BitmapDescriptor? = null
    private var driverIcon: BitmapDescriptor? = null

    // Theme state
    private var isDarkTheme = false
    private var currentThemeType: ThemeType? = null

    // Configuration change receiver
    private val configurationChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_CONFIGURATION_CHANGED) {
                if (currentThemeType == ThemeType.SYSTEM) {
                    viewModelScope.launch {
                        delay(200)
                        val newIsDarkTheme = isNightMode(context)

                        isDarkTheme = newIsDarkTheme
                        updateMapStyle(isDarkTheme)

                        viewModelScope.launch(Dispatchers.Main.immediate) {
                            clearIconCache()
                            initializeIcons()
                            redrawAllMapElements(container.stateFlow.value)
                        }
                    }
                }
            }
        }
    }

    private fun updateMapStyle(isDark: Boolean) {
        map?.let { googleMap ->
            if (isDark) {
                googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        appContext,
                        R.raw.google_dark_map
                    )
                )
            } else {
                googleMap.setMapStyle(null)
            }
        }
    }

    fun hasSavedCameraPosition(): Boolean = container.stateFlow.value.savedCameraPosition != null
    fun getSavedCameraPosition(): MapPoint? = container.stateFlow.value.savedCameraPosition

    init {
        initializeObservers()
        initializeThemeObserver()
    }

    private fun initializeObservers() {
        jobs += viewModelScope.launch {
            container.stateFlow
                .distinctUntilChangedBy {
                    listOf(
                        it.isMapReady,
                        it.googleMarkPaddingPx,
                        it.topPaddingPx,
                        it.bottomPaddingPx
                    )
                }
                .collectLatest { state ->
                    if (state.isMapReady) {
                        withContext(Dispatchers.Main.immediate) {
                            applyMapPadding(state)
                        }
                    }
                }
        }

        // Observe route changes
        jobs += viewModelScope.launch {
            container.stateFlow
                .distinctUntilChangedBy {
                    listOf(
                        it.route,
                        it.orderStatus
                    )
                }
                .collectLatest { state ->
                    withContext(Dispatchers.Main.immediate) {
                        updateRouteOnMap(state)
                    }
                }
        }

        // Observe location changes
        jobs += viewModelScope.launch {
            container.stateFlow
                .distinctUntilChangedBy {
                    listOf(
                        it.locations,
                        it.carArrivesInMinutes,
                        it.orderEndsInMinutes,
                        it.orderStatus
                    )
                }
                .collectLatest { state ->
                    withContext(Dispatchers.Main.immediate) {
                        updateMarkersOnMap(state)
                    }
                }
        }

        // Observe driver changes
        jobs += viewModelScope.launch {
            container.stateFlow
                .distinctUntilChangedBy { it.driver }
                .collectLatest { state ->
                    withContext(Dispatchers.Main.immediate) {
                        updateDriverOnMap(state)
                    }
                }
        }

        // Observe drivers list changes
        jobs += viewModelScope.launch {
            container.stateFlow
                .distinctUntilChangedBy { it.drivers }
                .collectLatest { state ->
                    withContext(Dispatchers.Main.immediate) {
                        updateDriversOnMap(state)
                    }
                }
        }

        jobs += viewModelScope.launch {
            container.stateFlow
                .distinctUntilChangedBy { it.orderStatus }
                .collectLatest { state ->
                    map?.uiSettings?.isScrollGesturesEnabled =
                        state.orderStatus !in OrderStatus.nonInteractive
                    map?.uiSettings?.isZoomGesturesEnabled =
                        state.orderStatus !in OrderStatus.nonInteractive
                    map?.uiSettings?.isTiltGesturesEnabled =
                        state.orderStatus !in OrderStatus.nonInteractive
                    map?.uiSettings?.isRotateGesturesEnabled =
                        state.orderStatus !in OrderStatus.nonInteractive
                }
        }
    }

    override fun onAppear() {
        // Initialize icons once at startup
        initializeIcons()

        // Register for configuration changes
        appContext.registerReceiver(
            configurationChangeReceiver,
            IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        )

        container.stateFlow.value.savedCameraPosition?.let { position ->
            map?.moveTo(position, MapConstants.DEFAULT_ZOOM)
        } ?: run {
            getCurrentLocation(context = appContext) { location ->
                map?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            location.latitude,
                            location.longitude
                        ),
                        MapConstants.DEFAULT_ZOOM
                    )
                )
            }
        }

        initializeObservers()
    }

    override fun onDisappear() {
        jobs.forEach { it.cancel() }
        jobs.clear()
        clearAllMapElements()

        // Unregister configuration change receiver
        try {
            appContext.unregisterReceiver(configurationChangeReceiver)
        } catch (e: Exception) {
            // Receiver might not be registered
        }

        // Don't clear icon cache on disappear to avoid recreating them
        // when the view reappears
    }

    fun onIntent(intent: MapsIntent) = intent {
        when (intent) {
            is MapsIntent.OnMapReady -> {
                viewModelScope.launch(Dispatchers.Main.immediate) {
                    map = intent.googleMap
                    configureMapSettings(intent.googleMap)

                    // Initialize icons early to avoid delays later
                    initializeIcons()

                    intent.googleMap.setOnCameraIdleListener { onCameraIdle() }
                    intent.googleMap.setOnCameraMoveStartedListener { reason ->
                        onCameraMoveStarted(reason)
                    }

                    intent { reduce { state.copy(isMapReady = true) } }
                    applyMapPadding(state)

                    // Initial draw of map elements
                    // Each subsequent update will be handled by the specific update methods
                    updateRouteOnMap(state)
                    updateMarkersOnMap(state)
                    updateDriverOnMap(state)
                    updateDriversOnMap(state)
                }
            }

            is MapsIntent.UpdateRoute -> intent {
                val oldRoute = state.route
                reduce { state.copy(route = intent.route) }

                if (oldRoute.isNotEmpty() && intent.route.isEmpty() && state.locations.isNotEmpty()) {
                    val firstLocation = state.locations.first()
                    viewModelScope.launch(Dispatchers.Main.immediate) {
                        map?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(firstLocation.lat, firstLocation.lng),
                                MapConstants.DEFAULT_ZOOM
                            )
                        )
                    }
                    intent { reduce { state.copy(savedCameraPosition = firstLocation) } }
                }

                if (intent.route.isEmpty()) {
                    viewModelScope.launch(Dispatchers.Main.immediate) {
                        clearAllMapElements()
                    }
                }
            }

            is MapsIntent.UpdateLocations -> reduce {
                state.copy(locations = intent.locations)
            }

            is MapsIntent.UpdateCarArrivesInMinutes -> reduce {
                state.copy(carArrivesInMinutes = intent.minutes)
            }

            is MapsIntent.UpdateOrderEndsInMinutes -> reduce {
                state.copy(orderEndsInMinutes = intent.minutes)
            }

            is MapsIntent.UpdateOrderStatus -> reduce {
                state.copy(orderStatus = intent.status)
            }

            is MapsIntent.MoveTo -> {
                viewModelScope.launch(Dispatchers.Main.immediate) {
                    map?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(intent.point.lat, intent.point.lng),
                            MapConstants.DEFAULT_ZOOM
                        )
                    )
                }
                reduce { state.copy(savedCameraPosition = intent.point) }
            }

            is MapsIntent.AnimateTo -> {
                viewModelScope.launch(Dispatchers.Main.immediate) {
                    map?.animateCamera(
                        CameraUpdateFactory.newLatLng(
                            LatLng(intent.point.lat, intent.point.lng)
                        )
                    )
                }
                reduce { state.copy(savedCameraPosition = intent.point) }
            }

            is MapsIntent.MoveToMyLocation -> {
                viewModelScope.launch(Dispatchers.Main.immediate) {
                    map?.let { googleMap ->
                        getCurrentLocation(intent.context) { location ->
                            val point = MapPoint(location.latitude, location.longitude)
                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(point.lat, point.lng),
                                    MapConstants.DEFAULT_ZOOM
                                )
                            )
                            intent { reduce { state.copy(savedCameraPosition = point) } }
                        }
                    }
                }
            }

            is MapsIntent.AnimateToMyLocation -> {
                viewModelScope.launch(Dispatchers.Main.immediate) {
                    map?.let { googleMap ->
                        getCurrentLocation(intent.context) { location ->
                            val point = MapPoint(location.latitude, location.longitude)
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(point.lat, point.lng),
                                    MapConstants.DEFAULT_ZOOM
                                )
                            )
                            intent { reduce { state.copy(savedCameraPosition = point) } }
                        }
                    }
                }
            }

            is MapsIntent.FitBounds -> {
                viewModelScope.launch(Dispatchers.Main.immediate) {
                    drawRoute(state.route, state.mapPaddingPx, animate = false)
                }
            }

            is MapsIntent.AnimateFitBounds -> {
                viewModelScope.launch(Dispatchers.Main.immediate) {
                    drawRoute(state.route, state.mapPaddingPx, animate = true)
                }
            }

            is MapsIntent.ZoomOut -> {
                viewModelScope.launch(Dispatchers.Main.immediate) {
                    map?.let { googleMap ->
                        val currentZoom = googleMap.cameraPosition.zoom
                        if (currentZoom > MapConstants.DEFAULT_ZOOM) {
                            googleMap.animateCamera(CameraUpdateFactory.zoomOut())
                        }
                    }
                }
            }

            is MapsIntent.SetGoogleMarkPadding -> reduce {
                state.copy(googleMarkPaddingPx = intent.paddingPx)
            }

            is MapsIntent.SetBottomPadding -> reduce {
                state.copy(bottomPaddingPx = dpToPx(appContext, intent.padding.value.toInt()))
            }

            is MapsIntent.SetTopPadding -> reduce {
                state.copy(topPaddingPx = dpToPx(appContext, intent.padding.value.toInt()))
            }

            is MapsIntent.SetMapPadding -> reduce {
                state.copy(mapPaddingPx = intent.paddingPx)
            }

            is MapsIntent.UpdateDriver -> reduce {
                state.copy(driver = intent.driver)
            }

            is MapsIntent.UpdateDrivers -> reduce {
                state.copy(drivers = intent.drivers)
            }
        }
    }

    private fun applyMapPadding(state: MapsState) {
        map?.setPadding(
            state.googleMarkPaddingPx,
            state.topPaddingPx,
            state.googleMarkPaddingPx,
            state.bottomPaddingPx
        )
    }

    // Initialize icons only once
    private fun initializeIcons() {
        if (originIcon == null) {
            originIcon = vectorToBitmapDescriptor(appContext, R.drawable.ic_origin_marker)
        }
        if (middleIcon == null) {
            middleIcon = vectorToBitmapDescriptor(appContext, R.drawable.ic_middle_marker)
        }
        if (destinationIcon == null) {
            destinationIcon = vectorToBitmapDescriptor(appContext, R.drawable.ic_destination_marker)
        }
        if (driverIcon == null) {
            driverIcon = vectorToBitmapDescriptorWithSize(appContext, R.drawable.img_car_marker, 48)
        }
    }

    private fun updateRouteOnMap(state: MapsState) {
        // Clear existing route
        polyline?.remove()
        polyline = null

        dashedPolylines.forEach { it.remove() }
        dashedPolylines.clear()

        // Only draw regular route when order status is NOT APPOINTED
        if (state.route.isNotEmpty() && state.orderStatus != OrderStatus.Appointed) {
            drawRoute(state.route, state.mapPaddingPx, animate = true)
        }

        // Only draw dashed connections when order status is NOT APPOINTED
        if (state.orderStatus != OrderStatus.Appointed && state.locations.isNotEmpty()) {
            drawDashedConnections(state.locations, state.route)
        }

        // Draw driver to first location connection if in APPOINTED state
        state.driver?.let { driver ->
            if (state.locations.isNotEmpty() && state.orderStatus == OrderStatus.Appointed) {
                drawDriverToFirstLocationConnection(driver, state.locations.first())
            }
        }
    }

    private fun updateMarkersOnMap(state: MapsState) {
        // Clear existing location markers
        markers.forEach { it.remove() }
        markers.clear()

        // Create info marker icons if needed
        createMarkerIcons(state.carArrivesInMinutes, state.orderEndsInMinutes)

        // Draw all location markers
        if (state.locations.isNotEmpty()) {
            drawAllMarkers(
                locations = state.locations,
                carArrivesInMinutes = state.carArrivesInMinutes,
                orderEndsInMinutes = state.orderEndsInMinutes,
                orderStatus = state.orderStatus,
                hasRoute = state.route.isNotEmpty() && state.orderStatus != OrderStatus.Appointed
            )
        }
    }

    // Keep track of the last driver position to avoid unnecessary updates
    private var lastDriverPosition: Pair<Double, Double>? = null
    private var lastDriverHeading: Float? = null

    private fun updateDriverOnMap(state: MapsState) {
        initializeIcons() // Ensure driver icon is initialized

        // Don't show driver when order status is not null, except for APPOINTED status
        if ((state.orderStatus != null && state.orderStatus != OrderStatus.Appointed) || state.driver == null) {
            // Order status is not null (except APPOINTED) or no driver in state, remove any existing markers
            driverMarkers.forEach { it.remove() }
            driverMarkers.clear()

            // Reset last known position and heading
            lastDriverPosition = null
            lastDriverHeading = null
            return
        }

        val driver = state.driver
        val currentPosition = Pair(driver.lat, driver.lng)
        val currentHeading = driver.heading.toFloat()

        // Only update if position or heading has changed
        if (lastDriverPosition != currentPosition || lastDriverHeading != currentHeading) {
            // Update existing marker if available, otherwise create new one
            if (driverMarkers.isNotEmpty()) {
                driverMarkers.first().apply {
                    // Update position and rotation smoothly
                    position = LatLng(driver.lat, driver.lng)
                    rotation = currentHeading
                }
            } else {
                // No existing marker, create a new one
                drawDriver(driver)
            }

            // Update last known position and heading
            lastDriverPosition = currentPosition
            lastDriverHeading = currentHeading
        }
    }

    // Keep track of the last drivers positions and headings using index as key
    private val lastDriversPositions = mutableMapOf<Int, Pair<Double, Double>>()
    private val lastDriversHeadings = mutableMapOf<Int, Float>()

    private fun updateDriversOnMap(state: MapsState) {
        initializeIcons() // Ensure driver icon is initialized

        // Don't show drivers when order status is not null
        if (state.drivers.isEmpty() || state.orderStatus != null) {
            // No drivers in state or order status is not null, remove any existing markers
            driversMarkers.forEach { it.remove() }
            driversMarkers.clear()
            lastDriversPositions.clear()
            lastDriversHeadings.clear()
            return
        }

        // Update existing markers or create new ones as needed
        val currentDrivers = state.drivers
        val currentMarkers = driversMarkers.toList()

        // Remove excess markers if we have more markers than drivers
        if (currentMarkers.size > currentDrivers.size) {
            for (i in currentDrivers.size until currentMarkers.size) {
                currentMarkers[i].remove()
                // Also remove from tracking maps
                lastDriversPositions.remove(i)
                lastDriversHeadings.remove(i)
            }
            driversMarkers.clear()
            driversMarkers.addAll(currentMarkers.take(currentDrivers.size))
        }

        // Update or create markers for each driver
        currentDrivers.forEachIndexed { index, driver ->
            val currentPosition = Pair(driver.lat, driver.lng)
            val currentHeading = driver.heading.toFloat()

            // Check if position or heading has changed
            val positionChanged = lastDriversPositions[index] != currentPosition
            val headingChanged = lastDriversHeadings[index] != currentHeading

            if (positionChanged || headingChanged || index >= currentMarkers.size) {
                if (index < currentMarkers.size) {
                    // Update existing marker only if position or heading changed
                    currentMarkers[index].apply {
                        position = LatLng(driver.lat, driver.lng)
                        rotation = currentHeading
                    }
                } else {
                    // Create new marker
                    map?.let { googleMap ->
                        val markerOptions = MarkerOptions()
                            .position(LatLng(driver.lat, driver.lng))
                            .flat(true)
                            .rotation(currentHeading)
                            .anchor(0.5f, 0.5f)
                            .zIndex(1f)
                            .icon(driverIcon ?: BitmapDescriptorFactory.defaultMarker())

                        googleMap.addMarker(markerOptions)?.let {
                            driversMarkers.add(it)
                        }
                    }
                }

                // Update last known position and heading
                lastDriversPositions[index] = currentPosition
                lastDriversHeadings[index] = currentHeading
            }
        }

        // Clean up any stored positions for indices that are no longer used
        val currentIndices = currentDrivers.indices.toSet()
        val storedIndices = lastDriversPositions.keys.toSet()

        storedIndices.minus(currentIndices).forEach { oldIndex ->
            lastDriversPositions.remove(oldIndex)
            lastDriversHeadings.remove(oldIndex)
        }
    }

    private fun redrawAllMapElements(state: MapsState) {
        clearAllMapElements()

        initializeIcons()

        updateRouteOnMap(state)
        updateMarkersOnMap(state)
        updateDriverOnMap(state)
        updateDriversOnMap(state)
    }

    private fun clearAllMapElements() {
        markers.forEach { it.remove() }
        markers.clear()

        driverMarkers.forEach { it.remove() }
        driverMarkers.clear()

        driversMarkers.forEach { it.remove() }
        driversMarkers.clear()

        polyline?.remove()
        polyline = null

        dashedPolylines.forEach { it.remove() }
        dashedPolylines.clear()
    }

    // Create a new driver marker (only called when no existing marker is available)
    private fun drawDriver(driver: Executor) {
        // Icon should already be initialized in initializeIcons()

        map?.let { googleMap ->
            val markerOptions = MarkerOptions()
                .position(LatLng(driver.lat, driver.lng))
                .flat(true)
                .rotation(driver.heading.toFloat())
                .anchor(0.5f, 0.5f)
                .zIndex(1f)
                .icon(driverIcon ?: BitmapDescriptorFactory.defaultMarker())

            googleMap.addMarker(markerOptions)?.let {
                driverMarkers.add(it)
            }
        }
    }

    // Create new driver markers (only called for drivers that don't have markers yet)
    private fun drawDrivers(drivers: List<Executor>) {
        // Icon should already be initialized in initializeIcons()

        map?.let { googleMap ->
            drivers.forEach { driver ->
                val markerOptions = MarkerOptions()
                    .position(LatLng(driver.lat, driver.lng))
                    .flat(true)
                    .rotation(driver.heading.toFloat())
                    .anchor(0.5f, 0.5f)
                    .zIndex(1f)
                    .icon(driverIcon ?: BitmapDescriptorFactory.defaultMarker())

                googleMap.addMarker(markerOptions)?.let {
                    driversMarkers.add(it)
                }
            }
        }
    }

    private fun drawRoute(route: List<MapPoint>, paddingPx: Int, animate: Boolean = true) {
        polyline?.remove()
        polyline = null
        if (route.size < 2) return

        map?.let { googleMap ->
            val latLngPoints = route.map { LatLng(it.lat, it.lng) }
            val boundsBuilder = LatLngBounds.Builder()
            route.forEach { point ->
                boundsBuilder.include(LatLng(point.lat, point.lng))
            }
            val bounds = boundsBuilder.build()

            val polylineColor = if (isDarkTheme) {
                MapConstants.POLYLINE_COLOR_NIGHT
            } else {
                MapConstants.POLYLINE_COLOR_DAY
            }

            polyline = googleMap.addPolyline(
                PolylineOptions()
                    .addAll(latLngPoints)
                    .color(polylineColor)
                    .width(MapConstants.POLYLINE_WIDTH)
            )

            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, paddingPx)
            if (container.stateFlow.value.orderStatus !in OrderStatus.nonInteractive)
                if (animate) googleMap.animateCamera(cameraUpdate)
                else googleMap.moveCamera(cameraUpdate)
        }
    }

    private fun drawAllMarkers(
        locations: List<MapPoint>,
        carArrivesInMinutes: Int?,
        orderEndsInMinutes: Int?,
        orderStatus: OrderStatus?,
        hasRoute: Boolean
    ) {
        if (locations.isEmpty()) return

        createMarkerIcons(carArrivesInMinutes, orderEndsInMinutes)

        map?.let { googleMap ->
            locations.forEachIndexed { index, point ->
                val markerOptions = MarkerOptions().position(LatLng(point.lat, point.lng))

                when (index) {
                    0 -> {
                        if (orderStatus != null || hasRoute) {
                            val icon = originInfoIcon ?: originIcon
                            ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                            markerOptions.icon(icon)
                            googleMap.addMarker(markerOptions)?.let { markers.add(it) }
                        }
                    }

                    locations.lastIndex -> {
                        val icon = destinationInfoIcon ?: destinationIcon
                        ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        markerOptions.icon(icon)
                        googleMap.addMarker(markerOptions)?.let { markers.add(it) }
                    }

                    else -> {
                        val icon = middleIcon
                            ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        markerOptions.icon(icon)
                        googleMap.addMarker(markerOptions)?.let { markers.add(it) }
                    }
                }
            }
        }
    }

    private fun createMarkerIcons(carArrivesInMinutes: Int?, orderEndsInMinutes: Int?) {
        originIcon = vectorToBitmapDescriptor(appContext, R.drawable.ic_origin_marker)
        middleIcon = vectorToBitmapDescriptor(appContext, R.drawable.ic_middle_marker)
        destinationIcon = vectorToBitmapDescriptor(appContext, R.drawable.ic_destination_marker)

        originInfoIcon = null
        destinationInfoIcon = null

        if (carArrivesInMinutes != null && container.stateFlow.value.orderStatus == null) {
            originInfoIcon = createInfoMarkerBitmapDescriptor(
                context = appContext,
                title = "$carArrivesInMinutes min",
                description = appContext.getString(R.string.coming),
                infoColor = appContext.resources.getColor(R.color.primary, appContext.theme),
                pointColor = appContext.resources.getColor(R.color.gray, appContext.theme),
                titleColor = appContext.resources.getColor(R.color.white, appContext.theme),
                descriptionColor = appContext.resources.getColor(R.color.white_50, appContext.theme)
            )
        }

        if (orderEndsInMinutes != null && container.stateFlow.value.orderStatus == null) {
            destinationInfoIcon = createInfoMarkerBitmapDescriptor(
                context = appContext,
                title = "$orderEndsInMinutes min",
                description = appContext.getString(R.string.on_the_way),
                infoColor = appContext.resources.getColor(R.color.black, appContext.theme),
                pointColor = appContext.resources.getColor(R.color.primary, appContext.theme),
                titleColor = appContext.resources.getColor(R.color.white, appContext.theme),
                descriptionColor = appContext.resources.getColor(R.color.white_50, appContext.theme)
            )
        }
    }

    private fun drawDashedConnections(
        locations: List<MapPoint>,
        route: List<MapPoint>
    ) {
        map?.let { googleMap ->
            locations.forEachIndexed { index, location ->
                val target: MapPoint? = when (index) {
                    0 -> route.firstOrNull()
                    locations.lastIndex -> route.lastOrNull()
                    else -> findClosestPointOnRoute(location, route)
                }

                if (target != null && target != location) {
                    val dashedPolyline = googleMap.addPolyline(
                        PolylineOptions()
                            .add(LatLng(location.lat, location.lng))
                            .add(LatLng(target.lat, target.lng))
                            .color(MapConstants.DASHED_POLYLINE_COLOR)
                            .width(MapConstants.DASHED_POLYLINE_WIDTH)
                            .pattern(listOf(Dash(16f), Gap(16f)))
                    )
                    dashedPolylines.add(dashedPolyline)
                }
            }
        }
    }

    private fun drawDriverToFirstLocationConnection(
        driver: Executor,
        firstLocation: MapPoint
    ) {
        map?.let { googleMap ->
            val driverPoint = MapPoint(driver.lat, driver.lng)

            if (driverPoint != firstLocation) {
                val polylineColor = if (isDarkTheme) {
                    MapConstants.POLYLINE_COLOR_NIGHT
                } else {
                    MapConstants.POLYLINE_COLOR_DAY
                }

                val solidPolyline = googleMap.addPolyline(
                    PolylineOptions()
                        .add(LatLng(driver.lat, driver.lng))
                        .add(LatLng(firstLocation.lat, firstLocation.lng))
                        .color(polylineColor)
                        .width(MapConstants.POLYLINE_WIDTH)
                )
                dashedPolylines.add(solidPolyline)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun configureMapSettings(googleMap: GoogleMap) {
        googleMap.uiSettings.apply {
            isCompassEnabled = false
            isMapToolbarEnabled = false
            isZoomControlsEnabled = false
            isMyLocationButtonEnabled = false
            isTiltGesturesEnabled = false
            isScrollGesturesEnabledDuringRotateOrZoom = false
        }

        val fineLocationPermission = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationPermission || coarseLocationPermission) {
            googleMap.isMyLocationEnabled = true
        }

        if (isDarkTheme) {
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    appContext,
                    R.raw.google_dark_map
                )
            )
        } else {
            googleMap.setMapStyle(null)
        }
    }

    private fun onCameraIdle() {
        if (polyline != null) return
        val position = map?.cameraPosition?.target?.let {
            MapPoint(it.latitude, it.longitude)
        } ?: return

        _cameraState.value = _cameraState.value.copy(
            position = position,
            isMoving = false
        )
    }

    private fun onCameraMoveStarted(reason: Int) {
        val isByUser = reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE
        _cameraState.value = _cameraState.value.copy(
            isMoving = true,
            isByUser = isByUser
        )
    }

    private fun initializeThemeObserver() {
        viewModelScope.launch {
            appPreferences.themeType.collect { themeType ->
                currentThemeType = themeType

                // Always get the current system theme when in SYSTEM mode
                val newIsDarkTheme = when (themeType) {
                    ThemeType.DARK -> true
                    ThemeType.LIGHT -> false
                    ThemeType.SYSTEM -> isNightMode(appContext)
                }

                // Always update isDarkTheme when theme type changes
                isDarkTheme = newIsDarkTheme
                updateMapStyle(isDarkTheme)

                if (map != null) {
                    viewModelScope.launch(Dispatchers.Main.immediate) {
                        clearIconCache()
                        initializeIcons()
                        redrawAllMapElements(container.stateFlow.value)
                    }
                }
            }
        }
    }

    private fun clearIconCache() {
        originIcon = null
        middleIcon = null
        destinationIcon = null
        originInfoIcon = null
        destinationInfoIcon = null
        driverIcon = null
    }

    private fun findClosestPointOnRoute(location: MapPoint, route: List<MapPoint>): MapPoint? {
        if (route.isEmpty()) return null

        return route.minByOrNull { routePoint ->
            val latDiff = location.lat - routePoint.lat
            val lngDiff = location.lng - routePoint.lng
            latDiff * latDiff + lngDiff * lngDiff
        }
    }
}

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun GoogleMap.setUp() {
    uiSettings.isCompassEnabled = false
    uiSettings.isMyLocationButtonEnabled = false
    uiSettings.isMapToolbarEnabled = false
    uiSettings.isRotateGesturesEnabled = true
    uiSettings.isTiltGesturesEnabled = false
    uiSettings.isZoomControlsEnabled = false
    uiSettings.isZoomGesturesEnabled = true
    uiSettings.isScrollGesturesEnabled = true
    uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = false

    mapType = GoogleMap.MAP_TYPE_NORMAL

    isIndoorEnabled = false
    isTrafficEnabled = false
    isBuildingsEnabled = false
    isMyLocationEnabled = true
}

fun GoogleMap.moveTo(
    point: MapPoint,
    zoom: Float? = null
) {
    zoom?.let {
        moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(point.lat, point.lng), zoom))
    } ?: run {
        moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(point.lat, point.lng),
                MapConstants.DEFAULT_ZOOM
            )
        )
    }
}
