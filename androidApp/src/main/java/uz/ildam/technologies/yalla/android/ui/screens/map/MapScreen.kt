package uz.ildam.technologies.yalla.android.ui.screens.map

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.requireSheetVisibleHeightDp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.ui.components.button.MapButton
import uz.ildam.technologies.yalla.android.ui.components.marker.YallaMarker
import uz.ildam.technologies.yalla.android.ui.sheets.OrderTaxiBottomSheet
import uz.ildam.technologies.yalla.android.ui.sheets.SheetValue
import uz.ildam.technologies.yalla.android.utils.vectorToBitmapDescriptor
import uz.ildam.technologies.yalla.android2gis.CameraState
import uz.ildam.technologies.yalla.android2gis.MapView

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MapScreen(
    uiState: MapUIState,
    isLoading: Boolean,
    scaffoldState: BottomSheetScaffoldState<SheetValue>,
    markerState: MarkerState,
    cameraPositionState: CameraPositionState,
    cameraState: CameraState,
    onIntent: (MapIntent) -> Unit
) {
    val context = LocalContext.current

    val startMarkerIcon = vectorToBitmapDescriptor(context, R.drawable.ic_origin_marker)
        ?: BitmapDescriptorFactory.defaultMarker()

    val middleMarkerIcon = vectorToBitmapDescriptor(context, R.drawable.ic_middle_marker)
        ?: BitmapDescriptorFactory.defaultMarker()

    val endMarkerIcon = vectorToBitmapDescriptor(context, R.drawable.ic_destination_marker)
        ?: BitmapDescriptorFactory.defaultMarker()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetDragHandle = null,
        sheetContainerColor = Color.Black,
        sheetShape = RectangleShape,
        sheetContent = {
            OrderTaxiBottomSheet(
                isLoading = isLoading,
                uiState = uiState,
                onCurrentLocationClick = { onIntent(MapIntent.SearchStartLocationSheet) },
                onDestinationClick = { onIntent(MapIntent.SearchEndLocationSheet) },
                onSetOptionsClick = { onIntent(MapIntent.OpenOptions) },
                onSelectTariff = { selectedTariff, wasSelected ->
                    onIntent(MapIntent.SelectTariff(selectedTariff, wasSelected))
                }
            )
        },
        content = {
            val bottomPadding by remember {
                derivedStateOf { scaffoldState.sheetState.requireSheetVisibleHeightDp() }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = remember(bottomPadding) { bottomPadding })
            ) {
//                MapView(
//                    modifier = Modifier.fillMaxSize(),
//                    cameraState = cameraState
//                ) {
//
//                }

                GoogleMap(
                    properties = uiState.properties,
                    uiSettings = uiState.mapUiSettings,
                    cameraPositionState = cameraPositionState,
                    modifier = Modifier.fillMaxSize(),
                    content = {
                        if (uiState.route.isEmpty()) {
                            Marker(state = markerState, alpha = 0f)
                        } else {
                            Polyline(points = uiState.route)

                            Marker(
                                state = remember(uiState.route.first()) {
                                    MarkerState(
                                        position = LatLng(
                                            uiState.route.first().latitude,
                                            uiState.route.first().longitude
                                        )
                                    )
                                },
                                icon = startMarkerIcon
                            )

                            uiState.destinations.dropLast(1).forEach { routePoint ->
                                if (routePoint.point != null)
                                    Marker(
                                        state = remember(routePoint) {
                                            MarkerState(
                                                position = LatLng(
                                                    routePoint.point.latitude,
                                                    routePoint.point.longitude
                                                )
                                            )
                                        },
                                        icon = middleMarkerIcon
                                    )
                            }

                            Marker(
                                state = remember(uiState.route.last()) {
                                    MarkerState(
                                        position = LatLng(
                                            uiState.route.last().latitude,
                                            uiState.route.last().longitude
                                        )
                                    )
                                },
                                icon = endMarkerIcon
                            )
                        }
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    if (uiState.route.isEmpty()) {
                        YallaMarker(
                            time = uiState.timeout,
                            isLoading = isLoading,
                            selectedAddressName = uiState.selectedLocation?.name,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    MapButton(
                        painter = painterResource(
                            if (uiState.moveCameraButtonState == MoveCameraButtonState.MyLocationView) R.drawable.ic_location
                            else R.drawable.ic_route
                        ),
                        modifier = Modifier.align(Alignment.BottomEnd),
                        onClick = {
                            if (uiState.moveCameraButtonState == MoveCameraButtonState.MyLocationView)
                                onIntent(MapIntent.MoveToMyLocation)
                            else
                                onIntent(MapIntent.MoveToMyRoute)
                        }
                    )

                    MapButton(
                        painter = painterResource(
                            if (uiState.discardOrderButtonState == DiscardOrderButtonState.OpenDrawer) R.drawable.ic_hamburger
                            else R.drawable.ic_arrow_back
                        ),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .statusBarsPadding(),
                        onClick = {
                            if (uiState.discardOrderButtonState == DiscardOrderButtonState.OpenDrawer)
                                onIntent(MapIntent.OpenDrawer)
                            else
                                onIntent(MapIntent.DiscardOrder)
                        }
                    )
                }
            }
        }
    )
}