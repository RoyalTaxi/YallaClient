package uz.ildam.technologies.yalla.android.ui.screens.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.cash.paging.compose.collectAsLazyPagingItems
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.android.ui.sheets.ArrangeDestinationsBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SearchByNameBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SheetValue
import uz.ildam.technologies.yalla.android.ui.sheets.TariffInfoBottomSheet
import uz.ildam.technologies.yalla.core.data.mapper.or0

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MapRoute(
    onPermissionDenied: () -> Unit,
    vm: MapViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by vm.uiState.collectAsState()

    var searchForLocationSheetVisibility by remember { mutableStateOf(false) }
    val searchForLocationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var arrangeDestinationsSheetVisibility by remember { mutableStateOf(false) }
    val arrangeDestinationsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var tariffBottomSheetVisibility by remember { mutableStateOf(false) }
    val tariffBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scaffoldState = rememberBottomSheetScaffoldState(
        sheetState = rememberBottomSheetState(
            confirmValueChange = { false },
            initialValue = SheetValue.Expanded,
            defineValues = {
                SheetValue.Collapsed at height(100.dp)
                SheetValue.PartiallyExpanded at height(200.dp)
                SheetValue.Expanded at contentHeight
            }
        )
    )
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberMarkerState()
    var isMarkerMoving by remember { mutableStateOf(false) }
    val currentLatLng = remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val permissionsGranted by rememberPermissionState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val orders = vm.ordersHistory.collectAsLazyPagingItems()

    LazyColumn {
        items(orders.itemCount) {
            Text("shown")
        }
    }

    LaunchedEffect(Unit) {
        vm.actionState.collectLatest { action ->
            when (action) {
                is MapActionState.AddressIdLoaded -> vm.fetchTariffs(action.id, currentLatLng.value)
                is MapActionState.PolygonLoaded -> vm.getAddressDetails(currentLatLng.value)
                is MapActionState.TariffsLoaded -> vm.getTimeout(currentLatLng.value)
                is MapActionState.AddressNameLoaded -> vm.updateSelectedLocation(name = action.name)

                else -> {}
            }
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
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
        onIntent = { },
        content = {
            MapScreen(
                uiState = uiState,
                isLoading = isMarkerMoving,
                scaffoldState = scaffoldState,
                markerState = markerState,
                cameraPositionState = cameraPositionState,
                onIntent = { intent ->
                    when (intent) {
                        is MapIntent.SelectTariff -> {
                            if (intent.wasSelected) scope.launch {
                                tariffBottomSheetVisibility = true
                                tariffBottomSheetState.show()
                            } else vm.updateUIState(selectedTariff = intent.tariff)
                        }

                        is MapIntent.MoveToMyLocation -> getCurrentLocation(context) { location ->
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

                        is MapIntent.OpenDrawer -> scope.launch { drawerState.open() }

                        is MapIntent.OpenDestinationLocationSheet -> {
                            scope.launch {
                                if (uiState.destinations.isEmpty()) {
                                    searchForLocationSheetVisibility = true
                                    searchForLocationSheetState.show()
                                } else {
                                    arrangeDestinationsSheetVisibility = true
                                    arrangeDestinationsSheetState.show()
                                }
                            }
                        }
                    }
                }
            )
        }
    )
    else onPermissionDenied()

    AnimatedVisibility(
        searchForLocationSheetVisibility,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
    ) {
        if (searchForLocationSheetVisibility) SearchByNameBottomSheet(
            sheetState = searchForLocationSheetState,
            foundAddresses = uiState.foundAddresses,
            onAddressSelected = { dest ->
                val destinations = uiState.destinations.toMutableList()
                destinations.add(MapUIState.Destination(dest.name, LatLng(dest.lat, dest.lng)))
                vm.updateUIState(destinations = destinations)
            },
            onSearchForAddress = { vm.searchForAddress(it, currentLatLng.value) },
            onDismissRequest = {
                scope.launch {
                    searchForLocationSheetVisibility = false
                    searchForLocationSheetState.hide()
                    vm.updateUIState(foundAddresses = emptyList())
                }
            }
        )
    }

    AnimatedVisibility(
        arrangeDestinationsSheetVisibility,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom) { it },
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom) { it }
    ) {
        if (arrangeDestinationsSheetVisibility) ArrangeDestinationsBottomSheet(
            destinations = uiState.destinations,
            sheetState = arrangeDestinationsSheetState,
            onAddNewDestinationClick = {
                scope.launch {
                    searchForLocationSheetVisibility = true
                    searchForLocationSheetState.show()
                }
            },
            onDismissRequest = { orderedDestinations ->
                scope.launch {
                    arrangeDestinationsSheetVisibility = false
                    arrangeDestinationsSheetState.hide()
                    vm.updateUIState(destinations = orderedDestinations)
                }
            }
        )
    }

    AnimatedVisibility(
        tariffBottomSheetVisibility,
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