package uz.ildam.technologies.yalla.android.ui.screens.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import uz.ildam.technologies.yalla.feature.map.domain.model.map.SearchForAddressItemModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTariffsModel

data class MapUIState(
    val markerSelectedLocation: SelectedLocation? = null,
    val selectedLocation: SelectedLocation? = null,
    val destinations: List<Destination> = emptyList(),
    val tariffs: GetTariffsModel? = null,
    val selectedTariff: GetTariffsModel.Tariff? = null,
    val timeout: Int? = 0,
    val foundAddresses: List<SearchForAddressItemModel> = emptyList(),
    val route: List<LatLng> = emptyList(),
    val options: List<GetTariffsModel.Tariff.Service> = emptyList(),
    val selectedOptions: List<GetTariffsModel.Tariff.Service> = emptyList(),
    val moveCameraButtonState: MoveCameraButtonState = MoveCameraButtonState.MyLocationView,
    val discardOrderButtonState: DiscardOrderButtonState = DiscardOrderButtonState.OpenDrawer,
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
) {
    data class SelectedLocation(
        val name: String?,
        val point: LatLng?,
        val addressId: Int?
    )

    data class Destination(
        val name: String?,
        val point: LatLng?
    )
}