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
import uz.ildam.technologies.yalla.android2gis.CameraPosition as Map2Gis

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MapRoute(
    onPermissionDenied: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onPaymentTypeClick: () -> Unit,
    vm: MapViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by vm.uiState.collectAsState()

    val searchLocationState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val destinationsState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val tariffState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val optionsState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val searchCarsState = rememberModalBottomSheetState(confirmValueChange = { false })

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scaffoldState = rememberBottomSheetScaffoldState(sheetState = rememberBottomSheetState(
        confirmValueChange = { false },
        initialValue = SheetValue.Expanded,
        defineValues = {
            SheetValue.Collapsed at contentHeight
            SheetValue.PartiallyExpanded at contentHeight
            SheetValue.Expanded at contentHeight
        }
    ))
    val googleCameraState = rememberCameraPositionState()
    val gisCameraState = rememberCameraState(Map2Gis(GeoPoint(49.545, 78.87), Zoom(2.0f)))
    val gisCameraNode by gisCameraState.node.collectAsState()
    var isMarkerMoving by remember { mutableStateOf(false) }
    val currentLatLng = remember { mutableStateOf(MapPoint(0.0, 0.0)) }
    val permissionsGranted by rememberPermissionState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val actionHandler by remember {
        mutableStateOf(
            MapActionHandler(
                mapType = AppPreferences.mapType,
                googleCameraState = googleCameraState,
                gisCameraState = gisCameraState
            )
        )
    }

    val bottomSheetHandler by remember {
        mutableStateOf(
            MapBottomSheetHandler(
                context = context,
                scope = scope,
                searchLocationState = searchLocationState,
                destinationsState = destinationsState,
                tariffState = tariffState,
                optionsState = optionsState,
                searchCarsState = searchCarsState,
                viewModel = vm
            )
        )
    }

    val sheetHandler by remember {
        mutableStateOf(
            MapSheetHandler(
                context = context,
                scope = scope,
                viewModel = vm,
                actionHandler = actionHandler,
                bottomSheetHandler = bottomSheetHandler
            )
        )
    }

    LaunchedEffect(Unit) {
        vm.actionState.collectLatest { action ->
            when (action) {
                is MapActionState.AddressIdLoaded -> vm.fetchTariffs(action.id)
                is MapActionState.PolygonLoaded -> vm.getAddressDetails(currentLatLng.value)
                is MapActionState.TariffsLoaded -> vm.getTimeout(currentLatLng.value)
                is MapActionState.AddressNameLoaded -> vm.updateSelectedLocation(name = action.name)

                else -> {}
            }
        }
    }

    LaunchedEffect(uiState.route) {
        actionHandler.moveCameraToFitBounds(
            routing = uiState.route,
            animate = true
        )
    }

    LaunchedEffect(isMarkerMoving) {
        if (uiState.route.isEmpty()) {
            if (isMarkerMoving) vm.changeStateToNotFound()
            else vm.getAddressDetails(currentLatLng.value)
        }
    }

    LaunchedEffect(permissionsGranted) {
        if (permissionsGranted) getCurrentLocation(context) { location ->
            currentLatLng.value = MapPoint(location.latitude, location.longitude)
            actionHandler.moveCamera(
                MapPoint(
                    lat = location.latitude,
                    lng = location.longitude
                )
            )
        }
    }

    LaunchedEffect(uiState.isSearchingForCars) {
        if (uiState.isSearchingForCars) sheetHandler.showSearchCars()
    }

    if (permissionsGranted) MapDrawer(
        drawerState = drawerState,
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is MapDrawerIntent.OrdersHistory -> onOrderHistoryClick()
                is MapDrawerIntent.PaymentType -> onPaymentTypeClick()
                else -> {

                }
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
                        is MapIntent.MoveToMyLocation -> getCurrentLocation(context) { location ->
                            currentLatLng.value = MapPoint(location.latitude, location.longitude)
                            actionHandler.moveCamera(
                                animate = true,
                                mapPoint = MapPoint(
                                    lat = location.latitude,
                                    lng = location.longitude
                                )
                            )
                        }

                        is MapIntent.MoveToMyRoute -> actionHandler.moveCameraToFitBounds(uiState.route)

                        is MapIntent.OpenDrawer -> scope.launch { drawerState.open() }

                        is MapIntent.DiscardOrder -> {
                            vm.updateDestinations(emptyList())
                            getCurrentLocation(context) { loc ->
                                currentLatLng.value = MapPoint(loc.latitude, loc.longitude)
                                actionHandler.moveCamera(
                                    animate = true,
                                    mapPoint = MapPoint(lat = loc.latitude, lng = loc.longitude)
                                )
                            }
                        }
                    }
                }
            )
        }
    ) else onPermissionDenied()

    bottomSheetHandler.Sheets(
        uiState = uiState,
        currentLatLng = currentLatLng,
        actionHandler = actionHandler
    )

    if (AppPreferences.mapType == MapType.Google) LaunchedEffect(googleCameraState.isMoving) {
        if (googleCameraState.isMoving.not())
            currentLatLng.value = googleCameraState.position.target.let {
                MapPoint(it.latitude, it.longitude)
            }
        isMarkerMoving = googleCameraState.isMoving
    }

    if (AppPreferences.mapType == MapType.Gis) DisposableEffect(gisCameraNode) {
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