package uz.ildam.technologies.yalla.android.ui.screens.map

import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import uz.ildam.technologies.yalla.core.domain.model.ExecutorModel
import uz.ildam.technologies.yalla.feature.map.domain.model.map.SearchForAddressItemModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.SettingModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTariffsModel

data class MapUIState(
    val markerSelectedLocation: SelectedLocation? = null,
    val selectedLocation: SelectedLocation? = null,
    val destinations: List<Destination> = emptyList(),
    val tariffs: GetTariffsModel? = null,
    val selectedTariff: GetTariffsModel.Tariff? = null,
    val timeout: Int? = null,
    val isSearchingForCars: Boolean = false,
    val setting: SettingModel? = null,
    val drivers: List<ExecutorModel> = emptyList(),
    val orders: List<Int> = emptyList(),
    val selectedOrder: Int? = null,
    val foundAddresses: List<SearchForAddressItemModel> = emptyList(),
    val route: List<MapPoint> = emptyList(),
    val options: List<GetTariffsModel.Tariff.Service> = emptyList(),
    val selectedOptions: List<GetTariffsModel.Tariff.Service> = emptyList(),
    val moveCameraButtonState: MoveCameraButtonState = MoveCameraButtonState.MyLocationView,
    val discardOrderButtonState: DiscardOrderButtonState = DiscardOrderButtonState.OpenDrawer,
    val mapUiSettings: MapUiSettings = MapUiSettings(
        compassEnabled = false,
        mapToolbarEnabled = false,
        zoomControlsEnabled = false,
        myLocationButtonEnabled = false,
        rotationGesturesEnabled = true,
        scrollGesturesEnabled = true,
        scrollGesturesEnabledDuringRotateOrZoom = false,
        tiltGesturesEnabled = false
    ),
    val properties: MapProperties = MapProperties(
        mapType = MapType.NORMAL,
        isBuildingEnabled = true,
        isMyLocationEnabled = true,
    )
) {
    data class SelectedLocation(
        val name: String?,
        val point: MapPoint?,
        val addressId: Int?
    )

    data class Destination(
        val name: String?,
        val point: MapPoint?
    )
}