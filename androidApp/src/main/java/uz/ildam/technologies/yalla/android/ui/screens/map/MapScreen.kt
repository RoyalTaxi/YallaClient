package uz.ildam.technologies.yalla.android.ui.screens.map

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberMarkerState
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.requireSheetVisibleHeightDp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.ui.components.button.MapButton
import uz.ildam.technologies.yalla.android.ui.components.marker.YallaMarker
import uz.ildam.technologies.yalla.android.ui.sheets.SheetValue
import uz.ildam.technologies.yalla.android.utils.vectorToBitmapDescriptor
import uz.ildam.technologies.yalla.android2gis.CameraState
import uz.ildam.technologies.yalla.android2gis.GeoPoint
import uz.ildam.technologies.yalla.android2gis.MapView
import uz.ildam.technologies.yalla.android2gis.Point
import uz.ildam.technologies.yalla.android2gis.imageFromResource
import uz.ildam.technologies.yalla.core.data.enums.MapType
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import com.google.maps.android.compose.Marker as GoogleMarker
import com.google.maps.android.compose.Polyline as GooglePolyline
import uz.ildam.technologies.yalla.android2gis.Marker as GisMarker
import uz.ildam.technologies.yalla.android2gis.Polyline as GisPolyline

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MapScreen(
    uiState: MapUIState,
    isLoading: Boolean,
    currentLatLng: MutableState<MapPoint>,
    scaffoldState: BottomSheetScaffoldState<SheetValue>,
    cameraPositionState: CameraPositionState,
    cameraState: CameraState,
    mapSheetHandler: MapSheetHandler,
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
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        sheetContent = {
            mapSheetHandler.Sheets(
                isLoading = isLoading,
                uiState = uiState,
                currentLatLng = currentLatLng
            )
        },
        content = {
            val bottomPadding by remember {
                derivedStateOf { scaffoldState.sheetState.requireSheetVisibleHeightDp() }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = remember(bottomPadding) { bottomPadding - 24.dp })
            ) {
                if (AppPreferences.mapType == MapType.Gis) MapView(
                    modifier = Modifier.fillMaxSize(),
                    cameraState = cameraState
                ) {
                    if (uiState.route.isEmpty()) Point(
                        position = GeoPoint(
                            latitude = cameraState.position.point.latitude,
                            longitude = cameraState.position.point.longitude
                        )
                    ) else {
                        GisPolyline(
                            points = uiState.route.map { GeoPoint(it.lat, it.lng) },
                            width = 4.dp
                        )

                        GisMarker(
                            icon = imageFromResource(R.drawable.ic_origin_marker),
                            position = GeoPoint(
                                latitude = uiState.route.first().lat,
                                longitude = uiState.route.first().lng
                            )
                        )

                        uiState.destinations.dropLast(1).forEach { routePoint ->
                            if (routePoint.point != null) GisMarker(
                                icon = imageFromResource(R.drawable.ic_middle_marker),
                                position = GeoPoint(
                                    latitude = uiState.route.first().lat,
                                    longitude = uiState.route.first().lng
                                )
                            )
                        }

                        GisMarker(
                            icon = imageFromResource(R.drawable.ic_destination_marker),
                            position = GeoPoint(
                                latitude = uiState.route.last().lat,
                                longitude = uiState.route.last().lng
                            )
                        )
                    }
                } else GoogleMap(
                    properties = uiState.properties,
                    uiSettings = uiState.mapUiSettings.copy(
                        scrollGesturesEnabled = !uiState.isSearchingForCars,
                        rotationGesturesEnabled = !uiState.isSearchingForCars,
                        zoomGesturesEnabled = !uiState.isSearchingForCars
                    ),
                    cameraPositionState = cameraPositionState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    content = {
                        if (uiState.route.isEmpty()) GoogleMarker(
                            state = rememberMarkerState(position = cameraPositionState.position.target),
                            alpha = 0f
                        )
                        else {
                            GooglePolyline(points = uiState.route.map { LatLng(it.lat, it.lng) })

                            GoogleMarker(
                                state = remember(uiState.route.first()) {
                                    MarkerState(
                                        position = LatLng(
                                            uiState.route.first().lat,
                                            uiState.route.first().lng
                                        )
                                    )
                                },
                                icon = startMarkerIcon
                            )

                            uiState.destinations.dropLast(1).forEach { routePoint ->
                                if (routePoint.point != null) GoogleMarker(
                                    state = remember(routePoint) {
                                        MarkerState(
                                            position = LatLng(
                                                routePoint.point.lat,
                                                routePoint.point.lng
                                            )
                                        )
                                    },
                                    icon = middleMarkerIcon
                                )
                            }

                            GoogleMarker(
                                state = remember(uiState.route.last()) {
                                    MarkerState(
                                        position = LatLng(
                                            uiState.route.last().lat,
                                            uiState.route.last().lng
                                        )
                                    )
                                },
                                icon = endMarkerIcon
                            )
                        }

                        uiState.drivers.forEach {
                            MarkerComposable(
                                flat = true,
                                state = remember(it) {
                                    MarkerState(
                                        position = LatLng(
                                            it.lat,
                                            it.lng
                                        )
                                    )
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.img_car_marker),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .padding(bottom = 24.dp)
                ) {
                    if (uiState.route.isEmpty()) {
                        YallaMarker(
                            time = uiState.timeout,
                            isLoading = isLoading || uiState.timeout == null,
                            isSearching = uiState.isSearchingForCars,
                            selectedAddressName = uiState.selectedLocation?.name,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    if (uiState.isSearchingForCars.not()) {
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
        }
    )
}