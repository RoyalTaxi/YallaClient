package uz.yalla.client.core.common.maps.core.viewmodel

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import uz.yalla.client.core.common.maps.core.controller.MapController
import uz.yalla.client.core.common.maps.core.handler.MapConfigHandler
import uz.yalla.client.core.common.maps.core.manager.MapElementManager
import uz.yalla.client.core.common.maps.core.manager.MapIconManager
import uz.yalla.client.core.common.maps.core.manager.MapThemeManager
import uz.yalla.client.core.common.maps.core.model.CameraState
import uz.yalla.client.core.common.maps.core.model.MapState
import uz.yalla.client.core.common.maps.core.model.MapsIntent
import uz.yalla.client.core.common.utils.dpToPx
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus

class MapsViewModel(
    private val appContext: Context,
    private val appPreferences: AppPreferences,
    private val iconManager: MapIconManager,
    private val elementManager: MapElementManager,
    private val themeManager: MapThemeManager,
    private val configHandler: MapConfigHandler,
    private val mapController: MapController
) : BaseViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        android.util.Log.e("MapsViewModel", "Coroutine error", throwable)
    }

    private val coroutineScope = CoroutineScope(
        Dispatchers.Main.immediate + SupervisorJob() + exceptionHandler
    )

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    private val _cameraState = MutableStateFlow(CameraState())
    val cameraState = _cameraState.asStateFlow()

    private val jobs = mutableListOf<Job>()
    private var mapRef: Any? = null

    init {
        initializeObservers()
        initializeThemeObserver()
        configHandler.registerReceiver()
        registerSystemThemeListener()
    }

    private fun initializeObservers() {
        jobs += coroutineScope.launch {
            state
                .distinctUntilChangedBy {
                    listOf(
                        it.isMapReady,
                        it.sideMarkPaddingPx,
                        it.topPaddingPx,
                        it.bottomPaddingPx
                    )
                }
                .collectLatest { current ->
                    if (current.isMapReady) withContext(Dispatchers.Main.immediate) {
                        applyMapPadding(current)
                    }
                }
        }

        jobs += coroutineScope.launch {
            state
                .distinctUntilChangedBy { listOf(it.route, it.order) }
                .collectLatest { current ->
                    withContext(Dispatchers.Main.immediate) { updateRouteOnMap(current) }
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
                .collectLatest { current ->
                    withContext(Dispatchers.Main.immediate) { updateMarkersOnMap(current) }
                }
        }

        jobs += coroutineScope.launch {
            state
                .distinctUntilChangedBy { it.driver }
                .collectLatest { current ->
                    withContext(Dispatchers.Main.immediate) { updateDriverOnMap(current) }
                }
        }

        jobs += coroutineScope.launch {
            state
                .distinctUntilChangedBy { it.drivers }
                .collectLatest { current ->
                    withContext(Dispatchers.Main.immediate) { updateDriversOnMap(current) }
                }
        }

        jobs += coroutineScope.launch {
            state
                .distinctUntilChangedBy { it.order }
                .collectLatest { current ->
                    val interactive = current.order?.status !in OrderStatus.nonInteractive
                    mapController.setGesturesEnabled(interactive)
                }
        }
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
                    mapController.attachMap(intent.map)
                    configureMapSettings(intent.map)
                    mapController.configureDefaultSettings(appContext)

                    // Wire camera callbacks from controller
                    mapController.setOnCameraIdle { pos ->
                        _cameraState.value = _cameraState.value.copy(
                            position = pos,
                            isMoving = false
                        )
                    }
                    mapController.setOnCameraMoveStarted { isByUser ->
                        _cameraState.value = _cameraState.value.copy(
                            isMoving = true,
                            isByUser = isByUser
                        )
                    }

                    _state.update { it.copy(isMapReady = true) }
                    applyMapPadding(state.value)

                    restartObservers()

                    // Initialize icons but do not fail flow if it throws
                    runCatching { iconManager.initializeIcons() }
                        .onFailure { android.util.Log.e("MapsViewModel", "Icon init failed", it) }

                    updateRouteOnMap(state.value)
                    updateMarkersOnMap(state.value)
                    updateDriverOnMap(state.value)
                    updateDriversOnMap(state.value)

                    // Choose initial camera without overriding route fit
                    val s = state.value
                    when {
                        s.savedCameraPosition != null -> {
                            mapController.moveTo(s.savedCameraPosition, null)
                        }

                        s.route.isNotEmpty() -> {
                            // Do nothing: drawRoute handled fitting to bounds
                        }

                        s.locations.size >= 2 -> {
                            // Let markers/dashed adjust; otherwise fallback to first
                        }

                        s.locations.isNotEmpty() -> {
                            mapController.moveTo(s.locations.first(), null)
                            _state.update { it.copy(savedCameraPosition = s.locations.first()) }
                        }

                        else -> {
                            mapController.moveToMyLocation(appContext)
                        }
                    }
                }
            }


            is MapsIntent.UpdateRoute -> {
                val old = state.value.route
                _state.update { it.copy(route = intent.route) }

                // If the route has been cleared, prefer focusing on driver rather than first location
                if (old.isNotEmpty() && intent.route.isEmpty()) {
                    val driver = state.value.driver
                    if (driver != null) {
                        val point = MapPoint(driver.lat, driver.lng)
                        coroutineScope.launch(Dispatchers.Main.immediate) { mapController.moveTo(point, null) }
                        _state.update { it.copy(savedCameraPosition = point) }
                    }
                }
            }

            is MapsIntent.UpdateLocations -> _state.update { it.copy(locations = intent.locations) }

            is MapsIntent.UpdateCarArrivesInMinutes -> _state.update { it.copy(carArrivesInMinutes = intent.minutes) }

            is MapsIntent.UpdateOrderEndsInMinutes -> _state.update { it.copy(orderEndsInMinutes = intent.minutes) }

            is MapsIntent.UpdateOrderStatus -> {
                _state.update { it.copy(order = intent.status) }
                updateDriversOnMap(state.value)
            }

            is MapsIntent.MoveTo -> {
                coroutineScope.launch(Dispatchers.Main.immediate) {
                    mapController.moveTo(
                        intent.point,
                        null
                    )
                }
                _state.update { it.copy(savedCameraPosition = intent.point) }
            }

            is MapsIntent.AnimateTo -> {
                coroutineScope.launch(Dispatchers.Main.immediate) { mapController.animateTo(intent.point) }
                _state.update { it.copy(savedCameraPosition = intent.point) }
            }

            is MapsIntent.MoveToMyLocation -> {
                coroutineScope.launch(Dispatchers.Main.immediate) {
                    getCurrentLocation(intent.context) { loc ->
                        val p = MapPoint(loc.latitude, loc.longitude)
                        mapController.moveTo(p, null)
                        _state.update { it.copy(savedCameraPosition = p) }
                    }
                }
            }

            is MapsIntent.AnimateToMyLocation -> {
                coroutineScope.launch(Dispatchers.Main.immediate) {
                    getCurrentLocation(intent.context) { loc ->
                        val p = MapPoint(loc.latitude, loc.longitude)
                        mapController.animateTo(p)
                        _state.update { it.copy(savedCameraPosition = p) }
                    }
                }
            }

            is MapsIntent.FitBounds -> coroutineScope.launch(Dispatchers.Main.immediate) {
                elementManager.updateRouteOnMap(
                    route = state.value.route,
                    locations = state.value.locations,
                    mapPaddingPx = state.value.mapPaddingPx,
                    leftPaddingPx = state.value.sideMarkPaddingPx,
                    topPaddingPx = state.value.topPaddingPx,
                    rightPaddingPx = state.value.sideMarkPaddingPx,
                    bottomPaddingPx = state.value.bottomPaddingPx,
                    isDarkTheme = themeManager.isDarkTheme,
                    orderStatus = state.value.order?.status,
                    animate = false
                )
            }

            is MapsIntent.AnimateFitBounds -> coroutineScope.launch(Dispatchers.Main.immediate) {
                elementManager.updateRouteOnMap(
                    route = state.value.route,
                    locations = state.value.locations,
                    mapPaddingPx = state.value.mapPaddingPx,
                    leftPaddingPx = state.value.sideMarkPaddingPx,
                    topPaddingPx = state.value.topPaddingPx,
                    rightPaddingPx = state.value.sideMarkPaddingPx,
                    bottomPaddingPx = state.value.bottomPaddingPx,
                    isDarkTheme = themeManager.isDarkTheme,
                    orderStatus = state.value.order?.status,
                    animate = true
                )
            }

            is MapsIntent.ZoomOut -> coroutineScope.launch(Dispatchers.Main.immediate) { mapController.zoomOut() }

            is MapsIntent.SetSideMarkPadding -> _state.update { it.copy(sideMarkPaddingPx = intent.paddingPx) }

            is MapsIntent.SetBottomPadding -> _state.update {
                it.copy(bottomPaddingPx = dpToPx(appContext, intent.padding.value.toInt()))
            }

            is MapsIntent.SetTopPadding -> _state.update {
                it.copy(topPaddingPx = dpToPx(appContext, intent.padding.value.toInt()))
            }

            is MapsIntent.SetMapPadding -> _state.update { it.copy(mapPaddingPx = intent.paddingPx) }

            is MapsIntent.UpdateDriver -> _state.update { it.copy(driver = intent.driver) }

            is MapsIntent.UpdateDrivers -> _state.update { it.copy(drivers = intent.drivers) }
        }
    }

    private fun applyMapPadding(state: MapState) {
        // Persist paddings so MapLibre can reapply after style reloads
        uz.yalla.client.core.common.maps.core.util.MapPaddingStore.set(
            state.sideMarkPaddingPx,
            state.topPaddingPx,
            state.sideMarkPaddingPx,
            state.bottomPaddingPx
        )
        mapController.setPadding(
            uz.yalla.client.core.common.maps.core.util.MapPaddingStore.left,
            uz.yalla.client.core.common.maps.core.util.MapPaddingStore.top,
            uz.yalla.client.core.common.maps.core.util.MapPaddingStore.right,
            uz.yalla.client.core.common.maps.core.util.MapPaddingStore.bottom
        )
    }

    private fun updateRouteOnMap(state: MapState) {
        elementManager.updateRouteOnMap(
            route = state.route,
            locations = state.locations,
            mapPaddingPx = state.mapPaddingPx,
            leftPaddingPx = state.sideMarkPaddingPx,
            topPaddingPx = state.topPaddingPx,
            rightPaddingPx = state.sideMarkPaddingPx,
            bottomPaddingPx = state.bottomPaddingPx,
            isDarkTheme = themeManager.isDarkTheme,
            orderStatus = state.order?.status
        )
    }

    private fun updateMarkersOnMap(state: MapState) {
        elementManager.updateMarkersOnMap(
            locations = state.locations,
            carArrivesInMinutes = state.carArrivesInMinutes,
            orderEndsInMinutes = state.orderEndsInMinutes,
            orderStatus = state.order?.status,
            hasOrder = state.order != null
        )
    }

    private fun updateDriverOnMap(state: MapState) {
        elementManager.updateDriverOnMap(state.driver)
    }

    private fun updateDriversOnMap(state: MapState) {
        elementManager.updateDriversOnMap(
            drivers = state.drivers,
            hasOrder = state.order != null
        )
    }

    private fun redrawAllMapElements(state: MapState) {
        elementManager.clearAllElements()
        runCatching { iconManager.initializeIcons() }
            .onFailure { android.util.Log.e("MapsViewModel", "Icon init failed (redraw)", it) }
        updateRouteOnMap(state)
        updateMarkersOnMap(state)
        updateDriverOnMap(state)
        updateDriversOnMap(state)
    }

    private fun configureMapSettings(map: Any) {
        mapRef = map
        elementManager.setMap(map)
        themeManager.applyMapStyle(appContext, map)
    }

    private fun initializeThemeObserver() {
        coroutineScope.launch {
            appPreferences.themeType.collect { themeType ->
                themeManager.updateTheme(appContext, themeType)
                reapplyStyleIfPossible()
                iconManager.clearCache()
                runCatching { iconManager.initializeIcons() }
                    .onFailure {
                        android.util.Log.e(
                            "MapsViewModel",
                            "Icon init failed (theme)",
                            it
                        )
                    }
                redrawAllMapElements(state.value)
            }
        }
    }

    private fun registerSystemThemeListener() {
        // Listen to system configuration changes to update map style when themeType == SYSTEM
        val filter = IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (themeManager.currentThemeType == uz.yalla.client.core.domain.model.type.ThemeType.SYSTEM) {
                    onExternalConfigurationChanged()
                }
            }
        }
        // Register on application context to avoid leaking an Activity
        appContext.registerReceiver(receiver, filter)
        // Track for cleanup
        systemConfigReceiver = receiver
    }

    fun onExternalConfigurationChanged() {
        // Called by MapConfigHandler or system receiver when configuration changes and ThemeType.SYSTEM is active
        coroutineScope.launch(Dispatchers.Main.immediate) {
            themeManager.updateTheme(appContext, uz.yalla.client.core.domain.model.type.ThemeType.SYSTEM)
            reapplyStyleIfPossible()
            iconManager.clearCache()
            runCatching { iconManager.initializeIcons() }
            redrawAllMapElements(state.value)
        }
    }

    fun onStartReapplyIfNeeded() {
        // When coming to foreground, if app theme follows system, ensure style matches current system theme
        if (themeManager.currentThemeType == uz.yalla.client.core.domain.model.type.ThemeType.SYSTEM) {
            onExternalConfigurationChanged()
        }
    }

    private var systemConfigReceiver: android.content.BroadcastReceiver? = null

    private fun reapplyStyleIfPossible() {
        mapRef?.let { themeManager.applyMapStyle(appContext, it) }
    }

    fun hasSavedCameraPosition(): Boolean = state.value.savedCameraPosition != null

    fun getSavedCameraPosition(): MapPoint? = state.value.savedCameraPosition

    override fun onCleared() {
        jobs.forEach { it.cancel() }
        jobs.clear()
        configHandler.unregisterReceiver()
        runCatching { appContext.unregisterReceiver(systemConfigReceiver) }
        super.onCleared()
    }
}
