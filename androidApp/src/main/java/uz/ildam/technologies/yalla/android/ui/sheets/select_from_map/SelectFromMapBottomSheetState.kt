package uz.ildam.technologies.yalla.android.ui.sheets.select_from_map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings

data class SelectFromMapBottomSheetState(
    val timeout: Int? = null,
    val name: String? = null,
    val latLng: LatLng? = null,
    val addressId: Int? = null,
    val mapUiSettings: MapUiSettings = MapUiSettings(
        compassEnabled = false,
        mapToolbarEnabled = false,
        zoomControlsEnabled = false,
        myLocationButtonEnabled = false
    ),
    val properties: MapProperties = MapProperties(
        mapType = MapType.NORMAL,
        isBuildingEnabled = true,
        isMyLocationEnabled = true,
    )
)