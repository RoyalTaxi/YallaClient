package uz.ildam.technologies.yalla.android.ui.screens.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import uz.ildam.technologies.yalla.android.ui.sheets.SheetValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MapRoute(
    vm: MapViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentLatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }

    val uiState by vm.uiState.collectAsState()
    val sheetState = rememberBottomSheetState(
        confirmValueChange = { false },
        initialValue = SheetValue.Expanded,
        defineValues = {
            SheetValue.Collapsed at height(100.dp)
            SheetValue.PartiallyExpanded at offset(percent = 60)
            SheetValue.Expanded at contentHeight
        }
    )
    val scaffoldState = rememberBottomSheetScaffoldState(sheetState)
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberMarkerState()
    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    var permissionsGranted by remember {
        mutableStateOf(locationPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        })
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            permissionsGranted = permissions.values.reduce { acc, isPermissionGranted ->
                acc && isPermissionGranted
            }
        }
    )

    LaunchedEffect(Unit) {
        launch { vm.getPolygon() }

        launch {
            vm.actionState.collectLatest { action ->
                when (action) {
                    is MapActionState.AddressIdLoaded -> {
                        vm.setSelectedAddressId(action.id)
                        vm.getTariffs(action.id, currentLatLng)
                    }

                    is MapActionState.AddressNameLoaded -> {}
                    is MapActionState.LoadingAddressId -> {}
                    is MapActionState.LoadingAddressName -> {}
                }
            }
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        launch {
            if (cameraPositionState.isMoving) {
                vm.setSelectedAddressId(id = null)
                vm.setSelectedAddressName(name = null)
            } else {
                cameraPositionState.position.target.apply {
                    markerState.position = this
                    currentLatLng = this
                }
                vm.getAddressId(currentLatLng)
                vm.getAddressName(currentLatLng)
            }
        }
    }

    LaunchedEffect(permissionsGranted) {
        launch {
            getCurrentLocation(context) { location ->
                scope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newCameraPosition(
                            CameraPosition(location, 15f, 0f, 0f)
                        ),
                        durationMs = 1000
                    )
                    vm.getAddressId(location)
                    vm.getAddressName(location)
                }
            }
        }
    }

    if (permissionsGranted) {
        MapScreen(
            uiState = uiState,
            scaffoldState = scaffoldState,
            sheetState = sheetState,
            markerState = markerState,
            cameraPositionState = cameraPositionState,
        )
    } else LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(locationPermissions)
    }
}

private fun getCurrentLocation(context: Context, onLocationFetched: (LatLng) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null)
                onLocationFetched(LatLng(location.latitude, location.longitude))
        }
    } catch (e: SecurityException) {

    }
}