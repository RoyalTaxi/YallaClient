package uz.yalla.client.core.common.maps.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.yalla.client.core.common.maps.handler.MapConfigurationHandler
import uz.yalla.client.core.common.maps.manager.MapElementManager
import uz.yalla.client.core.common.maps.manager.MapIconManager
import uz.yalla.client.core.common.maps.manager.MapThemeManager
import uz.yalla.client.core.common.maps.model.CameraState
import uz.yalla.client.core.common.maps.model.MapState
import uz.yalla.client.core.common.maps.model.MapsIntent
import uz.yalla.client.core.common.maps.util.MapConstants
import uz.yalla.client.core.common.utils.dpToPx
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus

class MapsViewModel(
    private val appContext: Context,
    private val appPreferences: AppPreferences,
    private val iconManager: MapIconManager = MapIconManager(appContext),
    private val mapElementManager: MapElementManager = MapElementManager(iconManager),
    private val themeManager: MapThemeManager = MapThemeManager()
) : BaseViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        // Error handling can be implemented here if needed
    }

    private val coroutineScope = CoroutineScope(
        Dispatchers.Main.immediate + SupervisorJob() + exceptionHandler
    )

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    private var map: GoogleMap? = null
    private val _cameraState = MutableStateFlow(CameraState())
    val cameraState = _cameraState.asStateFlow()

    private val jobs = mutableListOf<Job>()

    private val configurationHandler = MapConfigurationHandler(
        context = appContext,
        coroutineScope = coroutineScope,
        themeManager = themeManager,
        iconManager = iconManager,
        onConfigurationChanged = {
            if (state.value.isMapReady) {
                map?.let { googleMap ->
                    themeManager.applyMapStyle(appContext, googleMap)
                }
                redrawAllMapElements(state.value)
            }
        }
    )

    fun hasSavedCameraPosition(): Boolean = state.value.savedCameraPosition != null

    fun getSavedCameraPosition(): MapPoint? = state.value.savedCameraPosition

    init {
        initializeObservers()
        initializeThemeObserver()

        configurationHandler.registerReceiver()

        state.value.savedCameraPosition?.let { position ->
            map?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(position.lat, position.lng),
                    MapConstants.DEFAULT_ZOOM
                )
            )
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
    }

    private fun initializeObservers() {
        jobs += coroutineScope.launch {
            state
                .distinctUntilChangedBy {
                    listOf(
                        it.isMapReady,
                        it.googleMarkPaddingPx,
                        it.topPaddingPx,
                        it.bottomPaddingPx
                    )
                }
                .collectLatest { currentState ->
                    if (currentState.isMapReady) {
                        withContext(Dispatchers.Main.immediate) {
                            applyMapPadding(currentState)
                        }
                    }
                }
        }

        jobs += coroutineScope.launch {
            state
                .distinctUntilChangedBy {
                    listOf(
                        it.route,
                        it.order
                    )
                }
                .collectLatest { currentState ->
                    withContext(Dispatchers.Main.immediate) {
                        updateRouteOnMap(currentState)
                    }
                }
        }

        jobs += coroutineScope.launch {
            state
                .distinctUntilChangedBy {
                    listOf(
                        it.locations,
                        it.carArrivesInMinutes,
                        it.orderEndsInMinutes,
                        it.order
                    )
                }
                .collectLatest { currentState ->
                    withContext(Dispatchers.Main.immediate) {
                        updateMarkersOnMap(currentState)
                    }
                }
        }

        jobs += coroutineScope.launch {
            state
                .distinctUntilChangedBy { it.driver }
                .collectLatest { currentState ->
                    withContext(Dispatchers.Main.immediate) {
                        updateDriverOnMap(currentState)
                    }
                }
        }

        jobs += coroutineScope.launch {
            state
                .distinctUntilChangedBy { it.drivers }
                .collectLatest { currentState ->
                    withContext(Dispatchers.Main.immediate) {
                        updateDriversOnMap(currentState)
                    }
                }
        }

        jobs += coroutineScope.launch {
            state
                .distinctUntilChangedBy { it.order }
                .collectLatest { currentState ->
                    map?.uiSettings?.isScrollGesturesEnabled =
                        currentState.order?.status !in OrderStatus.nonInteractive
                    map?.uiSettings?.isZoomGesturesEnabled =
                        currentState.order?.status !in OrderStatus.nonInteractive
                    map?.uiSettings?.isTiltGesturesEnabled =
                        currentState.order?.status !in OrderStatus.nonInteractive
                    map?.uiSettings?.isRotateGesturesEnabled =
                        currentState.order?.status !in OrderStatus.nonInteractive
                }
        }
    }

    override fun onCleared() {
        jobs.forEach { it.cancel() }
        jobs.clear()

        mapElementManager.clearAllElements()

        configurationHandler.unregisterReceiver()

        mapElementManager.clearMap()
        map = null

        super.onCleared()
    }

    private fun restartObservers() {
        jobs.forEach { it.cancel() }
        jobs.clear()
        initializeObservers()
    }

    fun onIntent(intent: MapsIntent) {
        when (intent) {
            is MapsIntent.OnMapReady -> {
                coroutineScope.launch(Dispatchers.Main.immediate) {
                    map = intent.googleMap
                    mapElementManager.setMap(intent.googleMap)
                    configureMapSettings(intent.googleMap)

                    iconManager.initializeIcons()

                    intent.googleMap.setOnCameraIdleListener { onCameraIdle() }
                    intent.googleMap.setOnCameraMoveStartedListener { reason ->
                        onCameraMoveStarted(reason)
                    }

                    _state.update { it.copy(isMapReady = true) }
                    applyMapPadding(state.value)

                    restartObservers()

                    updateRouteOnMap(state.value)
                    updateMarkersOnMap(state.value)
                    updateDriverOnMap(state.value)
                    updateDriversOnMap(state.value)
                }
            }

            is MapsIntent.UpdateRoute -> {
                val oldRoute = state.value.route
                _state.update { it.copy(route = intent.route) }

                if (oldRoute.isNotEmpty() && intent.route.isEmpty() && state.value.locations.isNotEmpty()) {
                    val firstLocation = state.value.locations.first()
                    coroutineScope.launch(Dispatchers.Main.immediate) {
                        map?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(firstLocation.lat, firstLocation.lng),
                                MapConstants.DEFAULT_ZOOM
                            )
                        )
                    }
                    _state.update { it.copy(savedCameraPosition = firstLocation) }
                }

                if (intent.route.isEmpty()) {
                    coroutineScope.launch(Dispatchers.Main.immediate) {
                        mapElementManager.clearAllElements()
                    }
                }
            }

            is MapsIntent.UpdateLocations -> {
                _state.update { it.copy(locations = intent.locations) }
            }

            is MapsIntent.UpdateCarArrivesInMinutes -> {
                _state.update { it.copy(carArrivesInMinutes = intent.minutes) }
            }

            is MapsIntent.UpdateOrderEndsInMinutes -> {
                _state.update { it.copy(orderEndsInMinutes = intent.minutes) }
            }

            is MapsIntent.UpdateOrderStatus -> {
                _state.update { it.copy(order = intent.status) }
                updateDriversOnMap(state.value)
            }

            is MapsIntent.MoveTo -> {
                coroutineScope.launch(Dispatchers.Main.immediate) {
                    map?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(intent.point.lat, intent.point.lng),
                            MapConstants.DEFAULT_ZOOM
                        )
                    )
                }
                _state.update { it.copy(savedCameraPosition = intent.point) }
            }

            is MapsIntent.AnimateTo -> {
                coroutineScope.launch(Dispatchers.Main.immediate) {
                    map?.animateCamera(
                        CameraUpdateFactory.newLatLng(
                            LatLng(intent.point.lat, intent.point.lng)
                        )
                    )
                }
                _state.update { it.copy(savedCameraPosition = intent.point) }
            }

            is MapsIntent.MoveToMyLocation -> {
                coroutineScope.launch(Dispatchers.Main.immediate) {
                    map?.let { googleMap ->
                        getCurrentLocation(intent.context) { location ->
                            val point = MapPoint(location.latitude, location.longitude)
                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(point.lat, point.lng),
                                    MapConstants.DEFAULT_ZOOM
                                )
                            )
                            _state.update { it.copy(savedCameraPosition = point) }
                        }
                    }
                }
            }

            is MapsIntent.AnimateToMyLocation -> {
                coroutineScope.launch(Dispatchers.Main.immediate) {
                    map?.let { googleMap ->
                        getCurrentLocation(intent.context) { location ->
                            val point = MapPoint(location.latitude, location.longitude)
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(point.lat, point.lng),
                                    MapConstants.DEFAULT_ZOOM
                                )
                            )
                            _state.update { it.copy(savedCameraPosition = point) }
                        }
                    }
                }
            }

            is MapsIntent.FitBounds -> {
                coroutineScope.launch(Dispatchers.Main.immediate) {
                    mapElementManager.updateRouteOnMap(
                        route = state.value.route,
                        locations = state.value.locations,
                        mapPaddingPx = state.value.mapPaddingPx,
                        isDarkTheme = themeManager.isDarkTheme,
                        orderStatus = state.value.order?.status,
                        animate = false
                    )
                }
            }

            is MapsIntent.AnimateFitBounds -> {
                coroutineScope.launch(Dispatchers.Main.immediate) {
                    mapElementManager.updateRouteOnMap(
                        route = state.value.route,
                        locations = state.value.locations,
                        mapPaddingPx = state.value.mapPaddingPx,
                        isDarkTheme = themeManager.isDarkTheme,
                        orderStatus = state.value.order?.status,
                        animate = true
                    )
                }
            }

            is MapsIntent.ZoomOut -> {
                coroutineScope.launch(Dispatchers.Main.immediate) {
                    map?.let { googleMap ->
                        val currentZoom = googleMap.cameraPosition.zoom
                        if (currentZoom > MapConstants.MIN_ZOOM) {
                            googleMap.animateCamera(CameraUpdateFactory.zoomOut())
                        }
                    }
                }
            }

            is MapsIntent.SetGoogleMarkPadding -> {
                _state.update { it.copy(googleMarkPaddingPx = intent.paddingPx) }
            }

            is MapsIntent.SetBottomPadding -> {
                _state.update {
                    it.copy(
                        bottomPaddingPx = dpToPx(
                            appContext,
                            intent.padding.value.toInt()
                        )
                    )
                }
            }

            is MapsIntent.SetTopPadding -> {
                _state.update {
                    it.copy(
                        topPaddingPx = dpToPx(
                            appContext,
                            intent.padding.value.toInt()
                        )
                    )
                }
            }

            is MapsIntent.SetMapPadding -> {
                _state.update { it.copy(mapPaddingPx = intent.paddingPx) }
            }

            is MapsIntent.UpdateDriver -> {
                _state.update { it.copy(driver = intent.driver) }
            }

            is MapsIntent.UpdateDrivers -> {
                _state.update { it.copy(drivers = intent.drivers) }
            }
        }
    }

    private fun applyMapPadding(state: MapState) {
        map?.setPadding(
            state.googleMarkPaddingPx,
            state.topPaddingPx,
            state.googleMarkPaddingPx,
            state.bottomPaddingPx
        )
    }

    private fun updateRouteOnMap(state: MapState) {
        mapElementManager.updateRouteOnMap(
            route = state.route,
            locations = state.locations,
            mapPaddingPx = state.mapPaddingPx,
            isDarkTheme = themeManager.isDarkTheme,
            orderStatus = state.order?.status
        )
    }

    private fun updateMarkersOnMap(state: MapState) {
        mapElementManager.updateMarkersOnMap(
            locations = state.locations,
            carArrivesInMinutes = state.carArrivesInMinutes,
            orderEndsInMinutes = state.orderEndsInMinutes,
            orderStatus = state.order?.status,
            hasOrder = state.order != null
        )
    }

    private fun updateDriverOnMap(state: MapState) {
        mapElementManager.updateDriverOnMap(state.driver)
    }

    private fun updateDriversOnMap(state: MapState) {
        mapElementManager.updateDriversOnMap(
            drivers = state.drivers,
            hasOrder = state.order != null
        )
    }

    private fun redrawAllMapElements(state: MapState) {
        mapElementManager.clearAllElements()

        iconManager.initializeIcons()

        updateRouteOnMap(state)
        updateMarkersOnMap(state)
        updateDriverOnMap(state)
        updateDriversOnMap(state)
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

        themeManager.applyMapStyle(appContext, googleMap)
    }

    private fun onCameraIdle() {
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
        coroutineScope.launch {
            appPreferences.themeType.collect { themeType ->
                themeManager.updateTheme(appContext, themeType)

                map?.let { googleMap ->
                    themeManager.applyMapStyle(appContext, googleMap)
                }

                if (map != null && state.value.isMapReady) {
                    iconManager.clearCache()
                    iconManager.initializeIcons()
                    redrawAllMapElements(state.value)
                }
            }
        }
    }
}
