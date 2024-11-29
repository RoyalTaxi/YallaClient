package uz.ildam.technologies.yalla.android.ui.sheets.select_from_map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.MapButton
import uz.ildam.technologies.yalla.android.ui.components.button.SelectCurrentLocationButton
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.components.marker.YallaMarker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectFromMapBottomSheet(
    sheetState: SheetState,
    isForDestination: Boolean,
    onSelectLocation: (String, LatLng, Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: SelectFromMapBottomSheetViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberMarkerState()
    var isMarkerMoving by remember { mutableStateOf(false) }
    val currentLatLng = remember { mutableStateOf(LatLng(0.0, 0.0)) }


    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving) {
            viewModel.changeStateToNotFound()
        } else {
            val position = cameraPositionState.position.target
            currentLatLng.value = position
            markerState.position = position

            viewModel.getAddressDetails(position)
        }

        isMarkerMoving = cameraPositionState.isMoving
    }

    LaunchedEffect(Unit) {
        getCurrentLocation(context) { location ->
            viewModel.getAddressDetails(location)
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

    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = YallaTheme.color.gray2,
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(.8f)
                .fillMaxWidth()
                .background(YallaTheme.color.gray2)
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                GoogleMap(
                    properties = uiState.properties,
                    uiSettings = uiState.mapUiSettings,
                    cameraPositionState = cameraPositionState,
                    modifier = Modifier.matchParentSize(),
                    content = { Marker(state = markerState, alpha = 0f) }
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(20.dp)
                ) {
                    YallaMarker(
                        time = uiState.timeout,
                        isLoading = isMarkerMoving,
                        selectedAddressName = uiState.name,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    MapButton(
                        painter = painterResource(R.drawable.ic_location),
                        modifier = Modifier.align(Alignment.BottomEnd),
                        onClick = {
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
                    )

                    MapButton(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        modifier = Modifier.align(Alignment.TopStart),
                        onClick = onDismissRequest
                    )
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                    )
            ) {
                SelectCurrentLocationButton(
                    modifier = Modifier.padding(10.dp),
                    isLoading = isMarkerMoving,
                    currentLocation = uiState.name,
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
            ) {
                YallaButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    enabled = !isMarkerMoving && if (isForDestination) true else uiState.addressId != null,
                    text = stringResource(R.string.choose),
                    onClick = {
                        if (uiState.latLng != null && uiState.name != null) {
                            onSelectLocation(uiState.name!!, uiState.latLng!!, isForDestination)
                            onDismissRequest()
                        }
                    }
                )
            }
        }
    }
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