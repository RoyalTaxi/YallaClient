package uz.ildam.technologies.yalla.android.ui.screens.map

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.utils.getCurrentLocation
import uz.ildam.technologies.yalla.android.utils.pxToDp
import uz.ildam.technologies.yalla.android.utils.vectorToBitmapDescriptor
import uz.ildam.technologies.yalla.android2gis.dpToPx
import uz.ildam.technologies.yalla.core.data.mapper.or0
import uz.ildam.technologies.yalla.core.domain.model.MapPoint
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderStatus

class ConcreteGoogleMap : MapStrategy {
    override val isMarkerMoving: MutableState<Boolean> = mutableStateOf(false)
    override val mapPoint: MutableState<MapPoint> = mutableStateOf(MapPoint(0.0, 0.0))
    private lateinit var context: Context
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var cameraPositionState: CameraPositionState

    @Composable
    override fun Map(
        modifier: Modifier,
        uiState: MapUIState
    ) {
        context = LocalContext.current
        coroutineScope = rememberCoroutineScope()
        cameraPositionState = rememberCameraPositionState()

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

        LaunchedEffect(cameraPositionState.isMoving) {
            isMarkerMoving.value = cameraPositionState.isMoving
        }

        LaunchedEffect(cameraPositionState.position) {
            mapPoint.value = MapPoint(
                cameraPositionState.position.target.latitude,
                cameraPositionState.position.target.longitude
            )
        }

        GoogleMap(
            modifier = modifier,
            cameraPositionState = cameraPositionState,
            contentPadding = PaddingValues(
                top = pxToDp(context, WindowInsets.statusBars.getTop(LocalDensity.current)).dp,
                bottom = 20.dp
            ),
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isBuildingEnabled = true,
                isMyLocationEnabled = true,
            ),
            uiSettings = MapUiSettings(
                compassEnabled = false,
                mapToolbarEnabled = false,
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                rotationGesturesEnabled = true,
                scrollGesturesEnabled = true,
                scrollGesturesEnabledDuringRotateOrZoom = false,
                tiltGesturesEnabled = false
            )
        ) {
            if (
                uiState.selectedDriver?.status == OrderStatus.Appointed &&
                uiState.selectedDriver.status == OrderStatus.AtAddress
            ) Marker(
                icon = startMarkerIcon,
                state = remember(uiState.selectedLocation) {
                    MarkerState(
                        LatLng(
                            uiState.selectedLocation?.point?.lat.or0(),
                            uiState.selectedLocation?.point?.lng.or0(),
                        )
                    )
                }
            )

            if (uiState.route.isNotEmpty()) {
                Polyline(points = uiState.route.map { LatLng(it.lat, it.lng) })

                Marker(
                    icon = startMarkerIcon,
                    state = remember(uiState.route.first()) {
                        MarkerState(LatLng(uiState.route.first().lat, uiState.route.first().lng))
                    }
                )

                uiState.destinations.dropLast(1).forEach { routePoint ->
                    routePoint.point?.let {
                        Marker(
                            icon = middleMarkerIcon,
                            state = remember(routePoint) {
                                MarkerState(LatLng(it.lat, it.lng))
                            }
                        )
                    }
                }

                Marker(
                    icon = endMarkerIcon,
                    state = remember(uiState.route.last()) {
                        MarkerState(LatLng(uiState.route.last().lat, uiState.route.last().lng))
                    }
                )
            }

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

            uiState.drivers.take(20).forEach { driver ->
                MarkerComposable(
                    flat = true,
                    rotation = driver.heading.toFloat(),
                    state = remember(driver) { MarkerState(LatLng(driver.lat, driver.lng)) }
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
    }

    override fun move(to: MapPoint) {
        if (::cameraPositionState.isInitialized) {
            cameraPositionState.move(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(LatLng(to.lat, to.lng), 15f, 0.0f, 0.0f)
                )
            )
        }
    }

    override fun animate(to: MapPoint, durationMillis: Int) {
        if (::cameraPositionState.isInitialized) coroutineScope.launch {
            cameraPositionState.animate(
                durationMs = durationMillis,
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(LatLng(to.lat, to.lng), 15f, 0.0f, 0.0f)
                )
            )
        }
    }

    override fun moveToMyLocation() {
        if (::cameraPositionState.isInitialized) getCurrentLocation(context) { location ->
            val mapPoint = location.let { MapPoint(it.latitude, it.longitude) }
            move(mapPoint)
        }
    }

    override fun animateToMyLocation(durationMillis: Int) {
        if (::cameraPositionState.isInitialized) getCurrentLocation(context) { location ->
            val mapPoint = location.let { MapPoint(it.latitude, it.longitude) }
            animate(mapPoint, durationMillis)
        }
    }

    override fun moveToFitBounds(routing: List<MapPoint>) {
        if (::cameraPositionState.isInitialized && routing.isNotEmpty()) coroutineScope.launch {
            val boundsBuilder = LatLngBounds.Builder()
            routing.forEach { boundsBuilder.include(LatLng(it.lat, it.lng)) }
            val bounds = boundsBuilder.build()
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    dpToPx(context, 100)
                )
            )
        }
    }

    override fun animateToFitBounds(routing: List<MapPoint>) {
        if (::cameraPositionState.isInitialized && routing.isNotEmpty()) coroutineScope.launch {
            val boundsBuilder = LatLngBounds.Builder()
            routing.forEach { boundsBuilder.include(LatLng(it.lat, it.lng)) }
            val bounds = boundsBuilder.build()
            cameraPositionState.animate(
                durationMs = 1000,
                update = CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    dpToPx(context, 100)
                )
            )
        }
    }
}