package uz.ildam.technologies.yalla.android.ui.screens.map

import uz.ildam.technologies.yalla.core.data.enums.PaymentType
import uz.ildam.technologies.yalla.core.domain.model.Executor
import uz.ildam.technologies.yalla.core.domain.model.MapPoint
import uz.ildam.technologies.yalla.feature.map.domain.model.response.map.SearchForAddressItemModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.SettingModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.ShowOrderModel
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.ildam.technologies.yalla.feature.payment.domain.model.CardListItemModel
import uz.ildam.technologies.yalla.feature.profile.domain.model.response.GetMeModel

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
    val orders: List<ShowOrderModel> = emptyList(),
    val selectedOrder: ShowOrderModel? = null,
    val showingOrderId: Int? = null,

    // Payment-related properties
    val selectedPaymentType: PaymentType = PaymentType.CASH,
    val paymentTypes: List<CardListItemModel> = emptyList(),

    // Driver-related properties
    val drivers: List<Executor> = emptyList(),
    val selectedDriver: ShowOrderModel? = null,

    // Setting-related properties
    val setting: SettingModel? = null,
    val user: GetMeModel? = null,

    // UI State-related properties
    val moveCameraButtonState: MoveCameraButtonState = MoveCameraButtonState.MyLocationView,
    val discardOrderButtonState: DiscardOrderButtonState = DiscardOrderButtonState.OpenDrawer,
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