package uz.ildam.technologies.yalla.android.ui.screens.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import uz.ildam.technologies.yalla.android.ui.sheets.SearchByNameBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SheetValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MapRoute(
    onPermissionDenied: () -> Unit,
    vm: MapViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by vm.uiState.collectAsState()
    val searchForLocationSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var searchForLocationSheetVisibility by remember { mutableStateOf(false) }
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

    if (permissionsGranted) {
        MapScreen(
            uiState = uiState,
            isLoading = isMarkerMoving,
            scaffoldState = scaffoldState,
            markerState = markerState,
            cameraPositionState = cameraPositionState,
            onIntent = { intent ->
                when (intent) {
                    is MapIntent.SelectTariff -> vm.updateUIState(selectedTariff = intent.tariff)
                    is MapIntent.OpenDestinationLocationSearchSheet -> {
                        searchForLocationSheetVisibility = true
                        scope.launch { searchForLocationSheetState.show() }
                    }

                    is MapIntent.CloseDestinationLocationSearchSheet -> {
                        searchForLocationSheetVisibility = false
                        scope.launch { searchForLocationSheetState.hide() }
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

                    else -> {}
                }
            }
        )
    } else onPermissionDenied()

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
            vm.updateUIState(foundAddresses = emptyList())
            scope.launch { searchForLocationSheetState.hide() }
            searchForLocationSheetVisibility = false
        }
    )
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