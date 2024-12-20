package uz.ildam.technologies.yalla.android.ui.screens.map

import android.Manifest
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.maps.android.compose.rememberCameraPositionState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.dgis.sdk.map.CameraState
import ru.dgis.sdk.map.Zoom
import uz.ildam.technologies.yalla.android.ui.sheets.SheetValue
import uz.ildam.technologies.yalla.android.utils.getCurrentLocation
import uz.ildam.technologies.yalla.android.utils.rememberPermissionState
import uz.ildam.technologies.yalla.android2gis.DirectExecutor
import uz.ildam.technologies.yalla.android2gis.GeoPoint
import uz.ildam.technologies.yalla.android2gis.lat
import uz.ildam.technologies.yalla.android2gis.lon
import uz.ildam.technologies.yalla.android2gis.rememberCameraState
import uz.ildam.technologies.yalla.core.data.enums.MapType
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderStatus
import kotlin.time.Duration.Companion.seconds
import uz.ildam.technologies.yalla.android2gis.CameraPosition as Map2Gis

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MapRoute(
    onPermissionDenied: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onPaymentTypeClick: () -> Unit,
    onCancel: () -> Unit,
    onAddNewCard: () -> Unit,
    vm: MapViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Collect UI State
    val uiState by vm.uiState.collectAsState()

    // Bottom sheet and drawer states
    val searchLocationState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val destinationsState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val tariffState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val optionsState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val confirmCancellationState = rememberModalBottomSheetState(confirmValueChange = { false })
    val selectPaymentMethodState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Bottom sheet scaffold
    val scaffoldState = rememberBottomSheetScaffoldState(
        sheetState = rememberBottomSheetState(
            confirmValueChange = { false },
            initialValue = SheetValue.Expanded,
            defineValues = {
                SheetValue.Collapsed at contentHeight
                SheetValue.PartiallyExpanded at contentHeight
                SheetValue.Expanded at contentHeight
            }
        )
    )

    // Camera states for Google and 2GIS maps
    val googleCameraState = rememberCameraPositionState()
    val gisCameraState = rememberCameraState(Map2Gis(GeoPoint(49.545, 78.87), Zoom(2.0f)))
    val gisCameraNode by gisCameraState.node.collectAsState()

    // Marker movement state
    var isMarkerMoving by remember { mutableStateOf(false) }

    // Current LatLng of the user's location
    val currentLatLng = remember { mutableStateOf(MapPoint(0.0, 0.0)) }

    // Permission state
    val permissionsGranted by rememberPermissionState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // Handlers for map actions, bottom sheets, and sheets
    val actionHandler = remember {
        MapActionHandler(
            mapType = AppPreferences.mapType,
            googleCameraState = googleCameraState,
            gisCameraState = gisCameraState
        )
    }

    val bottomSheetHandler = remember {
        MapBottomSheetHandler(
            context = context,
            scope = scope,
            searchLocationState = searchLocationState,
            destinationsState = destinationsState,
            tariffState = tariffState,
            optionsState = optionsState,
            confirmCancellationState = confirmCancellationState,
            selectPaymentMethodState = selectPaymentMethodState,
            viewModel = vm
        )
    }

    val sheetHandler = remember {
        MapSheetHandler(
            viewModel = vm,
            bottomSheetHandler = bottomSheetHandler
        )
    }

    // Helper function to update currentLatLng with the user's current location and optionally move camera
    val updateLocationAndMoveCamera: (Boolean) -> Unit = { animate ->
        getCurrentLocation(context) { location ->
            currentLatLng.value = MapPoint(location.latitude, location.longitude)
            if (uiState.route.isEmpty()) {
                actionHandler.moveCamera(animate = animate, mapPoint = currentLatLng.value)
            } else {
                actionHandler.moveCameraToFitBounds(uiState.route, animate)
            }
        }
    }

    /** Collect Actions from ViewModel */
    LaunchedEffect(Unit) {
        vm.actionState.collectLatest { action ->
            when (action) {
                is MapActionState.AddressIdLoaded -> vm.fetchTariffs(action.id.toInt())
                is MapActionState.PolygonLoaded -> vm.getAddressDetails(currentLatLng.value)
                is MapActionState.TariffsLoaded -> vm.getTimeout(currentLatLng.value)
                is MapActionState.AddressNameLoaded -> vm.setSelectedLocation(name = action.name)
                else -> {}
            }
        }
    }

    /** Adjust camera on route changes */
    LaunchedEffect(uiState.route) {
        actionHandler.moveCameraToFitBounds(uiState.route, animate = true)
    }

    /**
     * If the marker stops moving and there's no route and the order isn't appointed,
     * fetch address details. If the marker is moving, reset state.
     */
    LaunchedEffect(isMarkerMoving) {
        if (uiState.route.isEmpty() && uiState.selectedDriver?.status != OrderStatus.Appointed) {
            if (isMarkerMoving) {
                vm.setNotFoundState()
            } else {
                vm.getAddressDetails(currentLatLng.value)
            }
        }
    }

    /**
     * Continuously poll getShow() every 5 seconds.
     * This could be considered for refactoring if an event-driven approach is possible.
     */
    LaunchedEffect(Unit) {
        while (true) {
            vm.getShow()
            delay(5.seconds)
        }
    }

    /** Update UI based on selected driver's order status */
    LaunchedEffect(uiState.selectedDriver?.status) {
        when (uiState.selectedDriver?.status) {
            OrderStatus.Appointed -> sheetHandler.showClientWaiting()
            OrderStatus.AtAddress -> sheetHandler.showDriverWaiting()
            OrderStatus.Canceled -> sheetHandler.showOrderTaxi()
            OrderStatus.InFetters -> sheetHandler.showOnTheRide()
            OrderStatus.Completed -> {}
            OrderStatus.New -> {
                uiState.selectedLocation?.point?.let { actionHandler.moveCamera(it) }
                sheetHandler.showSearchCars()
            }

            else -> sheetHandler.showOrderTaxi()
        }
    }

    /** If in fetters, move camera to driver's current location */
    LaunchedEffect(uiState.selectedDriver) {
        if (uiState.selectedDriver?.status == OrderStatus.InFetters)
            uiState.selectedDriver?.let { driver ->
                val driverPosition = MapPoint(
                    lat = driver.executor.coords.lat,
                    lng = driver.executor.coords.lng
                )
                actionHandler.moveCamera(
                    mapPoint = driverPosition,
                    animate = true,
                    duration = 1000
                )
            }
    }

    /** If permissions are granted, update location and camera */
    LaunchedEffect(permissionsGranted) {
        if (permissionsGranted) {
            updateLocationAndMoveCamera(false)
        }
    }

    /** Reset selected driver if there's no selected order */
    LaunchedEffect(uiState.selectedOrder) {
        if (uiState.selectedOrder == null) {
            vm.setSelectedDriver(driver = null)
        }
    }

    // If permissions are denied, show the appropriate UI or handle it
    if (!permissionsGranted) {
        onPermissionDenied()
        return
    }

    // Main UI: Drawer + MapScreen + Bottom sheets
    MapDrawer(
        drawerState = drawerState,
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is MapDrawerIntent.OrdersHistory -> onOrderHistoryClick()
                is MapDrawerIntent.PaymentType -> onPaymentTypeClick()
                else -> {}
            }
        },
        content = {
            MapScreen(
                uiState = uiState,
                isLoading = isMarkerMoving && uiState.route.isEmpty(),
                scaffoldState = scaffoldState,
                cameraPositionState = googleCameraState,
                cameraState = gisCameraState,
                mapSheetHandler = sheetHandler,
                currentLatLng = currentLatLng,
                onIntent = { intent ->
                    when (intent) {
                        is MapIntent.MoveToMyLocation -> updateLocationAndMoveCamera(true)
                        is MapIntent.MoveToMyRoute -> actionHandler.moveCameraToFitBounds(uiState.route)
                        is MapIntent.OpenDrawer -> scope.launch { drawerState.open() }
                        is MapIntent.DiscardOrder -> {
                            vm.setDestinations(emptyList())
                            updateLocationAndMoveCamera(true)
                        }
                    }
                }
            )
        }
    )

    // Bottom sheets
    bottomSheetHandler.Sheets(
        uiState = uiState,
        currentLatLng = currentLatLng,
        actionHandler = actionHandler,
        onAddNewCard = onAddNewCard,
        onCancel = {
            uiState.selectedOrder?.let { vm.cancelRide(it) }
            onCancel()
        }
    )

    // Handle marker movement updates for Google Maps
    if (AppPreferences.mapType == MapType.Google) {
        LaunchedEffect(googleCameraState.isMoving) {
            if (!googleCameraState.isMoving) {
                val pos = googleCameraState.position.target
                currentLatLng.value = MapPoint(pos.latitude, pos.longitude)
            }
            isMarkerMoving = googleCameraState.isMoving
        }
    }

    // Handle marker movement updates for 2GIS Maps
    if (AppPreferences.mapType == MapType.Gis) {
        DisposableEffect(gisCameraNode) {
            val node = gisCameraNode ?: return@DisposableEffect onDispose { }
            val closeable = node.stateChannel.connect(DirectExecutor) { newState ->
                isMarkerMoving = when (newState) {
                    CameraState.FREE -> {
                        currentLatLng.value =
                            gisCameraState.position.point.let { MapPoint(it.lat, it.lon) }
                        false
                    }

                    else -> true
                }
            }
            onDispose { closeable.close() }
        }
    }
}