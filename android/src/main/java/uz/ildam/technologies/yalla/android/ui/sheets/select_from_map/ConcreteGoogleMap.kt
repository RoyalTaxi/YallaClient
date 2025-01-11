package uz.ildam.technologies.yalla.android.ui.sheets.select_from_map

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.utils.getCurrentLocation
import uz.ildam.technologies.yalla.core.domain.model.MapPoint

class ConcreteGoogleMap : MapStrategy {
    override val isMarkerMoving: MutableState<Boolean> = mutableStateOf(false)
    override val mapPoint: MutableState<MapPoint> = mutableStateOf(MapPoint(0.0, 0.0))
    private lateinit var context: Context
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var cameraPositionState: CameraPositionState

    @Composable
    override fun Map(modifier: Modifier) {
        context = LocalContext.current
        coroutineScope = rememberCoroutineScope()
        cameraPositionState = rememberCameraPositionState()

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
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isBuildingEnabled = true,
                isMyLocationEnabled = true,
            ),
            uiSettings = MapUiSettings(
                compassEnabled = false,
                mapToolbarEnabled = false,
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            )
        )
    }

    override fun move(to: MapPoint) {
        if (::cameraPositionState.isInitialized) {
            cameraPositionState.move(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition(
                        LatLng(to.lat, to.lng),
                        15f,
                        0.0f,
                        0.0f
                    )
                )
            )
        }
    }

    override fun animate(to: MapPoint, durationMillis: Int) {
        if (::cameraPositionState.isInitialized) {
            coroutineScope.launch {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newCameraPosition(
                        CameraPosition(
                            LatLng(to.lat, to.lng),
                            15f,
                            0.0f,
                            0.0f
                        )
                    ),
                    durationMillis
                )
            }
        }
    }

    override fun moveToMyLocation() {
        if (::cameraPositionState.isInitialized) {
            getCurrentLocation(context) { location ->
                val mapPoint = location.let { MapPoint(it.latitude, it.longitude) }
                move(mapPoint)
            }
        }
    }

    override fun animateToMyLocation(durationMillis: Int) {
        if (::cameraPositionState.isInitialized) {
            getCurrentLocation(context) { location ->
                val mapPoint = location.let { MapPoint(it.latitude, it.longitude) }
                animate(mapPoint, durationMillis)
            }
        }
    }
}