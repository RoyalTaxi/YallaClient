package uz.ildam.technologies.yalla.android.ui.screens.map

import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings

data class MapState(
    val mapUiSettings: MapUiSettings = MapUiSettings(
        compassEnabled = false,
        mapToolbarEnabled = false,
        zoomControlsEnabled = false,
        myLocationButtonEnabled = false
    ),
    val properties: MapProperties = MapProperties(
        mapType = MapType.NORMAL,
        isBuildingEnabled = true,
        isIndoorEnabled = true,
        isMyLocationEnabled = true,
        isTrafficEnabled = true
    )
)