package uz.ildam.technologies.yalla.android.ui.screens.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android2gis.CameraPosition as Map2Gis
import org.koin.androidx.compose.koinViewModel
import ru.dgis.sdk.map.Zoom
import uz.ildam.technologies.yalla.android.ui.sheets.ArrangeDestinationsBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SearchByNameBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SetOrderOptionsBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SheetValue
import uz.ildam.technologies.yalla.android.ui.sheets.TariffInfoBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.select_from_map.SelectFromMapBottomSheet
import uz.ildam.technologies.yalla.android2gis.CameraState
import uz.ildam.technologies.yalla.android2gis.GeoPoint
import uz.ildam.technologies.yalla.android2gis.rememberCameraState
import uz.ildam.technologies.yalla.core.data.mapper.or0

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MapRoute(
    onPermissionDenied: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    vm: MapViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by vm.uiState.collectAsState()

    var searchForLocationSheetVisibility by remember {
        mutableStateOf(SearchForLocationBottomSheetVisibility.INVISIBLE)
    }
    val searchForLocationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectFromMapSheetVisibility by remember {
        mutableStateOf(SelectFromMapSheetVisibility.INVISIBLE)
    }
    val selectFromMapSheetSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { false }
    )
    var arrangeDestinationsSheetVisibility by remember { mutableStateOf(false) }
    val arrangeDestinationsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var tariffBottomSheetVisibility by remember { mutableStateOf(false) }
    val tariffBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var setOrderOptionsBottomSheetVisibility by remember { mutableStateOf(false) }
    val setOrderOptionsBottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val sheetState = rememberBottomSheetState(
        confirmValueChange = { false },
        initialValue = SheetValue.Expanded,
        defineValues = {
            SheetValue.Collapsed at contentHeight
            SheetValue.PartiallyExpanded at contentHeight
            SheetValue.Expanded at contentHeight
        }
    )
    val scaffoldState = rememberBottomSheetScaffoldState(sheetState = sheetState)
    val cameraPositionState = rememberCameraPositionState()
    val cameraState = rememberCameraState(Map2Gis(GeoPoint(49.545, 78.87), Zoom(2.0f)))
    val markerState = rememberMarkerState()
    var isMarkerMoving by remember { mutableStateOf(false) }
    val currentLatLng = remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val permissionsGranted by rememberPermissionState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

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
        if (uiState.route.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.Builder()
            uiState.route.forEach { boundsBuilder.include(it) }
            val bounds = boundsBuilder.build()

            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(bounds, 100),
                durationMs = 1000
            )
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (uiState.route.isEmpty()) {
            if (cameraPositionState.isMoving) {
                vm.changeStateToNotFound()
            } else {
                val position = cameraPositionState.position.target
                currentLatLng.value = position
                markerState.position = position

                vm.getAddressDetails(position)
            }

            isMarkerMoving = cameraPositionState.isMoving
        }
    }

    LaunchedEffect(permissionsGranted) {
        if (permissionsGranted) {
            getCurrentLocation(context) { location ->
                scope.launch {
                    currentLatLng.value = location
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newCameraPosition(
                            CameraPosition(location, 15f, 0f, 0f)
                        ),
                        durationMs = 1000
                    )
                }
            }
        }
    }

    if (permissionsGranted) MapDrawer(
        drawerState = drawerState,
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                MapDrawerIntent.OrdersHistory -> onOrderHistoryClick()
                else -> {

                }
            }
        },
        content = {
            MapScreen(
                uiState = uiState,
                isLoading = isMarkerMoving,
                scaffoldState = scaffoldState,
                markerState = markerState,
                cameraPositionState = cameraPositionState,
                cameraState = cameraState,
                onIntent = { intent ->
                    when (intent) {
                        is MapIntent.SelectTariff -> {
                            if (intent.wasSelected) scope.launch {
                                tariffBottomSheetVisibility = true
                                tariffBottomSheetState.show()
                            } else vm.updateSelectedTariff(intent.tariff)
                        }

                        is MapIntent.MoveToMyLocation -> {
                            getCurrentLocation(context) { location ->
                                scope.launch {
                                    cameraPositionState.animate(
                                        update = CameraUpdateFactory.newCameraPosition(
                                            CameraPosition(location, 15f, 0f, 0f)
                                        ),
                                        durationMs = 1000
                                    )
                                    currentLatLng.value = location
                                }
                            }
                        }

                        is MapIntent.MoveToMyRoute -> {
                            scope.launch {
                                if (uiState.route.isNotEmpty()) {
                                    val boundsBuilder = LatLngBounds.Builder()
                                    uiState.route.forEach { boundsBuilder.include(it) }
                                    val bounds = boundsBuilder.build()

                                    cameraPositionState.animate(
                                        update = CameraUpdateFactory.newLatLngBounds(bounds, 100),
                                        durationMs = 1000
                                    )
                                }
                            }
                        }

                        is MapIntent.OpenDrawer -> scope.launch { drawerState.open() }

                        is MapIntent.DiscardOrder -> {
                            vm.updateDestinations(emptyList())
                            getCurrentLocation(context) { location ->
                                scope.launch {
                                    cameraPositionState.animate(
                                        update = CameraUpdateFactory.newCameraPosition(
                                            CameraPosition(location, 15f, 0f, 0f)
                                        ),
                                        durationMs = 1000
                                    )
                                    currentLatLng.value = location
                                }
                            }
                        }

                        is MapIntent.SearchStartLocationSheet -> {
                            scope.launch {
                                searchForLocationSheetVisibility =
                                    SearchForLocationBottomSheetVisibility.START
                                searchForLocationSheetState.show()
                            }
                        }


                        is MapIntent.SearchEndLocationSheet -> {
                            scope.launch {
                                if (uiState.destinations.isEmpty()) {
                                    searchForLocationSheetVisibility =
                                        SearchForLocationBottomSheetVisibility.END
                                    searchForLocationSheetState.show()
                                } else {
                                    arrangeDestinationsSheetVisibility = true
                                    arrangeDestinationsSheetState.show()
                                }
                            }
                        }

                        MapIntent.OpenOptions -> {
                            scope.launch {
                                setOrderOptionsBottomSheetVisibility = true
                                setOrderOptionsBottomSheetState.show()
                            }
                        }
                    }
                }
            )
        }
    )
    else onPermissionDenied()

    AnimatedVisibility(
        visible = searchForLocationSheetVisibility != SearchForLocationBottomSheetVisibility.INVISIBLE,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
    ) {
        if (searchForLocationSheetVisibility != SearchForLocationBottomSheetVisibility.INVISIBLE) SearchByNameBottomSheet(
            sheetState = searchForLocationSheetState,
            foundAddresses = uiState.foundAddresses,
            isForDestination = searchForLocationSheetVisibility == SearchForLocationBottomSheetVisibility.END,
            onAddressSelected = { dest ->
                if (searchForLocationSheetVisibility == SearchForLocationBottomSheetVisibility.START) {
                    if (uiState.moveCameraButtonState == MoveCameraButtonState.MyRouteView) {
                        if (dest.addressId != 0) vm.getAddressDetails(LatLng(dest.lat, dest.lng))
                        else {
                            val result = vm.isPointInsidePolygon(LatLng(dest.lat, dest.lng))
                            if (result.first) {
                                vm.getAddressDetails(LatLng(dest.lat, dest.lng))
                                vm.updateSelectedLocation(addressId = result.second)
                            } else Toast.makeText(context, "Out of service", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        scope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newCameraPosition(
                                    CameraPosition(LatLng(dest.lat, dest.lng), 15f, 0f, 0f)
                                ),
                                durationMs = 1000
                            )
                            currentLatLng.value = LatLng(dest.lat, dest.lng)
                        }
                    }
                } else if (searchForLocationSheetVisibility == SearchForLocationBottomSheetVisibility.END) {
                    val destinations = uiState.destinations.toMutableList()
                    destinations.add(MapUIState.Destination(dest.name, LatLng(dest.lat, dest.lng)))
                    vm.updateDestinations(destinations)
                }
            },
            onSearchForAddress = { vm.searchForAddress(query = it, point = currentLatLng.value) },
            onClickMap = {
                scope.launch { searchForLocationSheetState.show() }
                selectFromMapSheetVisibility =
                    if (searchForLocationSheetVisibility == SearchForLocationBottomSheetVisibility.START) {
                        SelectFromMapSheetVisibility.START
                    } else {
                        SelectFromMapSheetVisibility.END
                    }
            },
            onDismissRequest = {
                scope.launch {
                    searchForLocationSheetVisibility =
                        SearchForLocationBottomSheetVisibility.INVISIBLE
                    searchForLocationSheetState.hide()
                    vm.updateUIState(foundAddresses = emptyList())
                }
            }
        )
    }

    AnimatedVisibility(
        visible = selectFromMapSheetVisibility != SelectFromMapSheetVisibility.INVISIBLE,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
    ) {
        if (selectFromMapSheetVisibility != SelectFromMapSheetVisibility.INVISIBLE) SelectFromMapBottomSheet(
            sheetState = selectFromMapSheetSheetState,
            isForDestination = selectFromMapSheetVisibility == SelectFromMapSheetVisibility.END,
            onSelectLocation = { name, location, isForDestination ->
                if (isForDestination) {
                    val destinations = uiState.destinations.toMutableList()
                    destinations.add(MapUIState.Destination(name, location))
                    vm.updateDestinations(destinations)
                } else {
                    if (uiState.moveCameraButtonState == MoveCameraButtonState.MyRouteView) {
                        vm.getAddressDetails(location)
                    } else {
                        scope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newCameraPosition(
                                    CameraPosition(location, 15f, 0f, 0f)
                                ),
                                durationMs = 1000
                            )
                            currentLatLng.value = location
                        }
                    }
                }
            },
            onDismissRequest = {
                scope.launch {
                    selectFromMapSheetVisibility = SelectFromMapSheetVisibility.INVISIBLE
                    searchForLocationSheetState.hide()
                }
            }
        )
    }

    AnimatedVisibility(
        visible = arrangeDestinationsSheetVisibility,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
    ) {
        if (arrangeDestinationsSheetVisibility) ArrangeDestinationsBottomSheet(
            destinations = uiState.destinations,
            sheetState = arrangeDestinationsSheetState,
            onAddNewDestinationClick = {
                scope.launch {
                    searchForLocationSheetVisibility = SearchForLocationBottomSheetVisibility.END
                    searchForLocationSheetState.show()
                }
            },
            onDismissRequest = { orderedDestinations ->
                scope.launch {
                    arrangeDestinationsSheetVisibility = false
                    arrangeDestinationsSheetState.hide()
                    vm.updateDestinations(orderedDestinations)
                    vm.fetchTariffs()

                    if (orderedDestinations.isEmpty()) getCurrentLocation(context) { location ->
                        scope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newCameraPosition(
                                    CameraPosition(location, 15f, 0f, 0f)
                                ),
                                durationMs = 1000
                            )
                            currentLatLng.value = location
                        }
                    }
                }
            }
        )
    }

    AnimatedVisibility(
        visible = tariffBottomSheetVisibility,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
    ) {
        uiState.selectedTariff?.let { selectedTariff ->
            if (tariffBottomSheetVisibility) TariffInfoBottomSheet(
                sheetState = tariffBottomSheetState,
                tariff = selectedTariff,
                arrivingTime = uiState.timeout.or0(),
                onDismissRequest = {
                    scope.launch {
                        tariffBottomSheetVisibility = false
                        tariffBottomSheetState.hide()
                    }
                }
            )
        }
    }

    AnimatedVisibility(
        visible = setOrderOptionsBottomSheetVisibility,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
    ) {
        uiState.selectedTariff?.let { selectedTariff ->
            if (setOrderOptionsBottomSheetVisibility) SetOrderOptionsBottomSheet(
                sheetState = setOrderOptionsBottomSheetState,
                selectedTariff = selectedTariff,
                options = uiState.options,
                selectedOptions = uiState.selectedOptions,
                onSave = { options -> vm.updateSelectedOptions(options) },
                onDismissRequest = {
                    scope.launch {
                        setOrderOptionsBottomSheetVisibility = false
                        setOrderOptionsBottomSheetState.hide()
                    }
                }
            )
        }
    }
}

@Composable
fun rememberPermissionState(permissions: List<String>): State<Boolean> {
    val context = LocalContext.current
    val arePermissionsGranted = remember {
        mutableStateOf(
            permissions.all { permission ->
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            arePermissionsGranted.value = result.values.all { it }
        }
    )

    LaunchedEffect(Unit) {
        if (!arePermissionsGranted.value) {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    return arePermissionsGranted
}

private fun getCurrentLocation(context: Context, onLocationFetched: (LatLng) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) return
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let { onLocationFetched(LatLng(it.latitude, it.longitude)) }
    }
}