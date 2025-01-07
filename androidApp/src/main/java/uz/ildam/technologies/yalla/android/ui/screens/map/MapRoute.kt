package uz.ildam.technologies.yalla.android.ui.screens.map

import android.Manifest
import android.app.Activity
import androidx.activity.compose.BackHandler
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
import uz.ildam.technologies.yalla.android.ui.dialogs.LoadingDialog
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
    onProfileClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onPaymentTypeClick: () -> Unit,
    onAddressesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCancel: () -> Unit,
    onAddNewCard: () -> Unit,
    vm: MapViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val uiState by vm.uiState.collectAsState()
    var loading by remember { mutableStateOf(false) }

    val searchLocationState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val destinationsState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val tariffState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val optionsState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val confirmCancellationState = rememberModalBottomSheetState(confirmValueChange = { false })
    val selectPaymentMethodState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val orderCommentState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val activeOrdersState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

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

    val googleCameraState = rememberCameraPositionState()
    val gisCameraState = rememberCameraState(Map2Gis(GeoPoint(49.545, 78.87), Zoom(2.0f)))
    val gisCameraNode = gisCameraState.node.collectAsState()

    var isMarkerMoving by remember { mutableStateOf(false) }

    val currentLatLng = remember { mutableStateOf(MapPoint(0.0, 0.0)) }

    val permissionsGranted by rememberPermissionState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val actionHandler = remember {
        MapActionHandler(
            context = context,
            mapType = AppPreferences.mapType,
            googleCameraState = googleCameraState,
            gisCameraState = gisCameraState,
            gisCameraNode = gisCameraNode
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
            orderCommentState = orderCommentState,
            activeOrdersState = activeOrdersState,
            viewModel = vm
        )
    }

    val sheetHandler = remember {
        MapSheetHandler(
            viewModel = vm,
            bottomSheetHandler = bottomSheetHandler
        )
    }

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

    BackHandler {
        if (uiState.route.isNotEmpty()) {
            vm.setDestinations(emptyList())
            uiState.selectedLocation?.point?.let { there -> actionHandler.moveCamera(there, true) }
        } else if (uiState.selectedDriver == null) {
            (context as? Activity)?.finish()
        }
    }

    LaunchedEffect(Unit) {
        launch {
            while (true) {
                vm.getActiveOrders()
                delay(5.seconds)
            }
        }
    }

    LaunchedEffect(Unit) {
        launch {
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
    }

    LaunchedEffect(uiState.route) {
        launch {
            actionHandler.moveCameraToFitBounds(uiState.route, animate = true)
        }
    }

    LaunchedEffect(isMarkerMoving) {
        launch {
            if (uiState.route.isEmpty() && uiState.selectedDriver?.status != OrderStatus.Appointed) {
                if (isMarkerMoving) vm.setNotFoundState()
                else vm.getAddressDetails(currentLatLng.value)
            }
        }
    }

    LaunchedEffect(uiState.showingOrderId) {
        launch {
            while (uiState.showingOrderId != null) {
                vm.getShow()
                delay(5.seconds)
            }
        }
    }

    LaunchedEffect(uiState.selectedDriver?.status) {
        launch {
            when (uiState.selectedDriver?.status) {
                OrderStatus.Appointed -> sheetHandler.showClientWaiting()
                OrderStatus.AtAddress -> sheetHandler.showDriverWaiting()
                OrderStatus.Canceled -> sheetHandler.showOrderTaxi()
                OrderStatus.InFetters -> sheetHandler.showOnTheRide()
                OrderStatus.Completed -> sheetHandler.showFeedback()
                OrderStatus.New -> {
                    uiState.selectedLocation?.point?.let { actionHandler.moveCamera(it) }
                    sheetHandler.showSearchCars()
                }

                OrderStatus.NonStopSending -> {
                    uiState.selectedLocation?.point?.let { actionHandler.moveCamera(it) }
                    sheetHandler.showSearchCars()
                }

                OrderStatus.Sending -> {
                    uiState.selectedLocation?.point?.let { actionHandler.moveCamera(it) }
                    sheetHandler.showSearchCars()
                }

                OrderStatus.UserSending -> {
                    uiState.selectedLocation?.point?.let { actionHandler.moveCamera(it) }
                    sheetHandler.showSearchCars()
                }

                null -> sheetHandler.showOrderTaxi()
            }
        }
    }

    LaunchedEffect(uiState.selectedDriver) {
        launch {
            if (uiState.selectedDriver != null) loading = false
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
    }

    LaunchedEffect(permissionsGranted) {
        launch { vm.getMe() }

        launch {
            if (permissionsGranted) {
                updateLocationAndMoveCamera(true)
            }
        }
    }

    LaunchedEffect(uiState.selectedOrder) {
        launch {
            if (uiState.selectedOrder == null) {
                vm.setSelectedDriver(driver = null)
            }
        }
    }

    if (!permissionsGranted) {
        onPermissionDenied()
        return
    }

    MapDrawer(
        drawerState = drawerState,
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is MapDrawerIntent.Profile -> onProfileClick()
                is MapDrawerIntent.OrdersHistory -> onOrderHistoryClick()
                is MapDrawerIntent.PaymentType -> onPaymentTypeClick()
                is MapDrawerIntent.MyPlaces -> onAddressesClick()
                is MapDrawerIntent.Settings -> onSettingsClick()
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
                onCreateOrder = { loading = true },
                mapBottomSheetHandler = bottomSheetHandler,
                activeOrdersState = activeOrdersState,
                onIntent = { intent ->
                    when (intent) {
                        is MapIntent.MoveToMyLocation -> updateLocationAndMoveCamera(true)
                        is MapIntent.MoveToMyRoute -> {
                            vm.updateCameraButton(MoveCameraButtonState.FirstLocation)
                            actionHandler.moveCameraToFitBounds(uiState.route)
                        }

                        is MapIntent.MoveToFirstLocation -> {
                            vm.updateCameraButton(MoveCameraButtonState.MyRouteView)
                            actionHandler.moveCamera(
                                mapPoint = uiState.route.first(),
                                animate = true,
                                duration = 1000
                            )
                        }

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

    bottomSheetHandler.Sheets(
        uiState = uiState,
        currentLatLng = currentLatLng,
        actionHandler = actionHandler,
        onAddNewCard = onAddNewCard,
        onCancel = {
            uiState.showingOrderId?.let { vm.cancelRide(it) }
            onCancel()
        }
    )

    if (AppPreferences.mapType == MapType.Google) {
        LaunchedEffect(googleCameraState.isMoving) {
            if (!googleCameraState.isMoving) {
                val pos = googleCameraState.position.target
                currentLatLng.value = MapPoint(pos.latitude, pos.longitude)
            }
            isMarkerMoving = googleCameraState.isMoving
        }
    }

    if (AppPreferences.mapType == MapType.Gis) {
        DisposableEffect(gisCameraState.position) {
            val node = gisCameraNode.value ?: return@DisposableEffect onDispose { }
            val closeable = node.stateChannel.connect(DirectExecutor) { newState ->
                currentLatLng.value = gisCameraState.position.point.let { MapPoint(it.lat, it.lon) }
                isMarkerMoving = when (newState) {
                    CameraState.FREE -> false
                    else -> true
                }
            }
            onDispose { closeable.close() }
        }
    }

    if (loading) LoadingDialog()
}