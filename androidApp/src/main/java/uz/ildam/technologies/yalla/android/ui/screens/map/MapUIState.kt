package uz.ildam.technologies.yalla.android.ui.screens.map

import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import uz.ildam.technologies.yalla.core.data.enums.PaymentType
import uz.ildam.technologies.yalla.core.domain.model.ExecutorModel
import uz.ildam.technologies.yalla.feature.map.domain.model.map.SearchForAddressItemModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.SettingModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.ShowOrderModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.ildam.technologies.yalla.feature.payment.domain.model.CardListItemModel

data class MapUIState(

    // Location-related properties
    val markerSelectedLocation: SelectedLocation? = null,
    val selectedLocation: SelectedLocation? = null,
    val destinations: List<Destination> = emptyList(),
    val foundAddresses: List<SearchForAddressItemModel> = emptyList(),
    val route: List<MapPoint> = emptyList(),

    // Tariff-related properties
    val tariffs: GetTariffsModel? = null,
    val selectedTariff: GetTariffsModel.Tariff? = null,
    val options: List<GetTariffsModel.Tariff.Service> = emptyList(),
    val selectedOptions: List<GetTariffsModel.Tariff.Service> = emptyList(),
    val comment: String = "",

    // Order-related properties
    val timeout: Int? = null,
    // val isSearchingForCars: Boolean = false,
    val orders: List<Int> = emptyList(),
    val selectedOrder: Int? = null,

    // Payment-related properties
    val selectedPaymentType: PaymentType = PaymentType.CASH,
    val paymentTypes: List<CardListItemModel> = emptyList(),

    // Driver-related properties
    val drivers: List<ExecutorModel> = emptyList(),
    val selectedDriver: ShowOrderModel? = null,

    // Setting-related properties
    val setting: SettingModel? = null,

    // UI State-related properties
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