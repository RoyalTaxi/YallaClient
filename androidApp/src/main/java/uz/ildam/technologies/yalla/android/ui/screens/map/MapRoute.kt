package uz.ildam.technologies.yalla.android.ui.screens.map

import android.Manifest
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.android.ui.sheets.SheetValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MapRoute(
    vm: MapViewModel = koinViewModel()
) {
    val context = LocalContext.current
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

    val mapState by vm.mapState.collectAsState()

    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        defineValues = {
            SheetValue.Collapsed at height(100.dp)
            SheetValue.PartiallyExpanded at offset(percent = 60)
            SheetValue.Expanded at contentHeight
        }
    )

    val scaffoldState = rememberBottomSheetScaffoldState(sheetState)
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberMarkerState()

    LaunchedEffect(cameraPositionState.isMoving) {
        launch { markerState.position = cameraPositionState.position.target }
    }

    LaunchedEffect(Unit) {
        launch { vm.getPolygon() }
    }

    if (permissionsGranted) MapScreen(
        scaffoldState = scaffoldState,
        sheetState = sheetState,
        mapState = mapState,
        markerState = markerState,
        cameraPositionState = cameraPositionState
    )
    else LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(locationPermissions)
    }
}