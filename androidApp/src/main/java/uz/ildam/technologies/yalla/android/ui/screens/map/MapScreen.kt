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
import com.google.android.gms.maps.model.BitmapDescriptor
import uz.ildam.technologies.yalla.android2gis.imageFromResource
import uz.ildam.technologies.yalla.core.data.enums.MapType
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderStatus
import kotlin.math.abs
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

    // Extract commonly used states
    val driverStatus = uiState.selectedDriver?.status
    val routeEmpty = uiState.route.isEmpty()

    // Convert vector resources to bitmap descriptors for Google Map markers
    val startMarkerIcon = remember {
        vectorToBitmapDescriptor(context, R.drawable.ic_origin_marker)
            ?: BitmapDescriptorFactory.defaultMarker()
    }
    val middleMarkerIcon = remember {
        vectorToBitmapDescriptor(context, R.drawable.ic_middle_marker)
            ?: BitmapDescriptorFactory.defaultMarker()
    }
    val endMarkerIcon = remember {
        vectorToBitmapDescriptor(context, R.drawable.ic_destination_marker)
            ?: BitmapDescriptorFactory.defaultMarker()
    }

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
            val adjustedBottomPadding = remember(bottomPadding) { abs(bottomPadding.value - 24).dp }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = adjustedBottomPadding)
            ) {
                // Show either GisMapContent or GoogleMapContent depending on the selected map type
                if (AppPreferences.mapType == MapType.Gis) {
                    GisMapContent(uiState, cameraState)
                } else {
                    GoogleMapContent(
                        uiState = uiState,
                        cameraPositionState = cameraPositionState,
                        startMarkerIcon = startMarkerIcon,
                        middleMarkerIcon = middleMarkerIcon,
                        endMarkerIcon = endMarkerIcon
                    )
                }

                // Overlay UI elements (Marker, Buttons)
                MapOverlay(
                    uiState = uiState,
                    isLoading = isLoading,
                    routeEmpty = routeEmpty,
                    driverStatus = driverStatus,
                    onIntent = onIntent
                )
            }
        }
    )
}

@Composable
private fun GisMapContent(
    uiState: MapUIState,
    cameraState: CameraState
) {
    val routeEmpty = uiState.route.isEmpty()
    MapView(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState
    ) {
        if (routeEmpty) {
            Point(position = cameraState.position.point)
        } else {
            GisPolyline(
                points = uiState.route.map { GeoPoint(it.lat, it.lng) },
                width = 4.dp
            )

            // Start marker
            GisMarker(
                icon = imageFromResource(R.drawable.ic_origin_marker),
                position = GeoPoint(uiState.route.first().lat, uiState.route.first().lng)
            )

            // Middle markers
            uiState.destinations.dropLast(1).forEach { routePoint ->
                routePoint.point?.let {
                    GisMarker(
                        icon = imageFromResource(R.drawable.ic_middle_marker),
                        position = GeoPoint(it.lat, it.lng)
                    )
                }
            }

            // End marker
            GisMarker(
                icon = imageFromResource(R.drawable.ic_destination_marker),
                position = GeoPoint(uiState.route.last().lat, uiState.route.last().lng)
            )
        }
    }
}

@Composable
private fun GoogleMapContent(
    uiState: MapUIState,
    cameraPositionState: CameraPositionState,
    startMarkerIcon: BitmapDescriptor,
    middleMarkerIcon: BitmapDescriptor,
    endMarkerIcon: BitmapDescriptor
) {
    val driverStatus = uiState.selectedDriver?.status

    GoogleMap(
        properties = uiState.properties,
        uiSettings = uiState.mapUiSettings.copy(
            scrollGesturesEnabled = driverStatus != OrderStatus.New,
            rotationGesturesEnabled = driverStatus != OrderStatus.New,
            zoomGesturesEnabled = driverStatus != OrderStatus.New
        ),
        cameraPositionState = cameraPositionState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        MapMarkers(uiState, startMarkerIcon, middleMarkerIcon, endMarkerIcon)
    }
}

@Composable
private fun MapMarkers(
    uiState: MapUIState,
    startMarkerIcon: BitmapDescriptor,
    middleMarkerIcon: BitmapDescriptor,
    endMarkerIcon: BitmapDescriptor
) {
    val routeEmpty = uiState.route.isEmpty()

    // If order appointed and location available
    if (uiState.selectedDriver?.status == OrderStatus.Appointed && uiState.selectedLocation?.point != null) {
        val startPosition =
            if (routeEmpty) uiState.selectedLocation.point else uiState.route.first()
        GoogleMarker(
            icon = startMarkerIcon,
            state = remember(startPosition) {
                MarkerState(LatLng(startPosition.lat, startPosition.lng))
            }
        )
    }

    if (!routeEmpty) {
        // Draw the route
        GooglePolyline(points = uiState.route.map { LatLng(it.lat, it.lng) })

        GoogleMarker(
            icon = startMarkerIcon,
            state = remember(uiState.route.first()) {
                MarkerState(LatLng(uiState.route.first().lat, uiState.route.first().lng))
            }
        )

        // Middle markers
        uiState.destinations.dropLast(1).forEach { routePoint ->
            routePoint.point?.let {
                GoogleMarker(
                    icon = middleMarkerIcon,
                    state = remember(routePoint) {
                        MarkerState(LatLng(it.lat, it.lng))
                    }
                )
            }
        }

        // End marker
        GoogleMarker(
            icon = endMarkerIcon,
            state = remember(uiState.route.last()) {
                MarkerState(LatLng(uiState.route.last().lat, uiState.route.last().lng))
            }
        )
    }

    // Selected driver marker
    uiState.selectedDriver?.let {
        MarkerComposable(
            flat = true,
            rotation = it.executor.coords.heading.toFloat(),
            state = remember(it) {
                MarkerState(LatLng(it.executor.coords.lat, it.executor.coords.lng))
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

    // Other drivers markers
    uiState.drivers.forEach { driver ->
        MarkerComposable(
            flat = true,
            rotation = driver.heading.toFloat(),
            state = remember(driver) {
                MarkerState(LatLng(driver.lat, driver.lng))
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

@Composable
private fun MapOverlay(
    uiState: MapUIState,
    isLoading: Boolean,
    routeEmpty: Boolean,
    driverStatus: OrderStatus?,
    onIntent: (MapIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .padding(bottom = 24.dp)
    ) {
        YallaMarker(
            time = uiState.timeout,
            isLoading = isLoading,
            isSearching = driverStatus == OrderStatus.New,
            isRouteEmpty = routeEmpty,
            isAppointed = driverStatus == OrderStatus.Appointed,
            isAtAddress = driverStatus == OrderStatus.AtAddress,
            isInFetters = driverStatus == OrderStatus.InFetters,
            selectedAddressName = uiState.selectedLocation?.name,
            modifier = Modifier.align(Alignment.Center)
        )

        // Show buttons if driver is not in NEW status
        if (driverStatus != OrderStatus.New) {
            // Move camera button (my location or route view)
            MapButton(
                painter = painterResource(
                    if (uiState.moveCameraButtonState == MoveCameraButtonState.MyLocationView)
                        R.drawable.ic_location
                    else
                        R.drawable.ic_route
                ),
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = {
                    if (uiState.moveCameraButtonState == MoveCameraButtonState.MyLocationView)
                        onIntent(MapIntent.MoveToMyLocation)
                    else
                        onIntent(MapIntent.MoveToMyRoute)
                }
            )

            // Drawer or discard order button
            MapButton(
                painter = painterResource(
                    if (uiState.discardOrderButtonState == DiscardOrderButtonState.OpenDrawer)
                        R.drawable.ic_hamburger
                    else
                        R.drawable.ic_arrow_back
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