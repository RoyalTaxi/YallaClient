package uz.ildam.technologies.yalla.android.ui.screens.map

import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import uz.ildam.technologies.yalla.feature.order.domain.model.tarrif.GetTariffsModel

data class MapUIState(
    val selectedAddressName: String? = null,
    val selectedAddressId: Int? = null,
    val tariffs: GetTariffsModel? = null,
    val selectedTariff: GetTariffsModel.Tariff? = null,
    val timeout: Int? = 0,
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