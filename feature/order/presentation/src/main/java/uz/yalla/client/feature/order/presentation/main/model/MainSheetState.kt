package uz.yalla.client.feature.order.presentation.main.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.sheet.search_address.SearchByNameSheetValue
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapViewValue
import uz.yalla.client.core.domain.model.PaymentType
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.SelectedLocation
import uz.yalla.client.core.domain.model.ServiceModel
import uz.yalla.client.feature.map.domain.model.response.PolygonRemoteItem
import uz.yalla.client.feature.order.domain.model.request.OrderTaxiDto
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.feature.payment.domain.model.CardListItemModel

data class MainSheetState(
    val polygon: List<PolygonRemoteItem> = emptyList(),
    val loading: Boolean = false,

    val sheetHeight: Dp = 0.dp,
    val footerHeight: Dp = 0.dp,

    val tariffs: GetTariffsModel? = null,
    val selectedTariff: GetTariffsModel.Tariff? = null,

    val timeout: Int? = null,
    val drivers: List<Executor> = emptyList(),

    val selectedLocationId:Int? = null,
    val selectedLocation: SelectedLocation? = null,
    val destinations: List<Destination> = emptyList(),

    val selectedOptions: List<ServiceModel> = emptyList(),
    val options: List<ServiceModel> = emptyList(),

    val comment: String = "",
    val cardList: List<CardListItemModel> = emptyList(),
    val selectedPaymentType: PaymentType = AppPreferences.paymentType,
    val selectedService: String = "road",

    val order: ShowOrderModel? = null,
    val orderId: Int? = null,
    val isSecondaryAddressMandatory: Boolean = false,
    val shadowVisibility: Boolean = false,
    val isPaymentMethodSheetVisible: Boolean = false,
    val isOrderCommentSheetVisible: Boolean = false,
    val searchByNameSheetVisible: SearchByNameSheetValue = SearchByNameSheetValue.INVISIBLE,
    val selectFromMapViewVisible: SelectFromMapViewValue = SelectFromMapViewValue.INVISIBLE,
    val addDestinationSheetVisible: Boolean = false
) {
    fun getBadgeText(): String? = when {
        selectedOptions.isNotEmpty() -> selectedOptions.size.toString()
        comment.isNotBlank() -> ""
        else -> null
    }

    fun mapToOrderTaxiDto(): OrderTaxiDto? {
        val addressId = selectedLocationId ?: return null
        val from = selectedLocation ?: return null
        val fromLat = selectedLocation.point?.lat ?: return null
        val fromLng = selectedLocation.point?.lng ?: return null
        val to = destinations
            .filter { it.point?.lat != null && it.point?.lng != null }
            .map { dest ->
                OrderTaxiDto.Address(
                    addressId = null,
                    lat = dest.point!!.lat,
                    lng = dest.point!!.lng,
                    name = dest.name.orEmpty()
                )
            }
        val selectedTariff = selectedTariff ?: return null
        val selectedCardId = (selectedPaymentType as? PaymentType.CARD)?.cardId
        val selectedOptionsIds = selectedOptions.map { it.id }
        return OrderTaxiDto(
            dontCallMe = false,
            service = selectedService,
            cardId = selectedCardId,
            addressId = addressId,
            toPhone = AppPreferences.number,
            comment = comment,
            tariffId = selectedTariff.id,
            tariffOptions = selectedOptionsIds,
            paymentType = selectedPaymentType.typeName,
            fixedPrice = selectedTariff.fixedType,
            addresses = listOf(
                OrderTaxiDto.Address(
                    addressId = selectedLocation.addressId,
                    lat = fromLat,
                    lng = fromLng,
                    name = from.name.orEmpty()
                ),
                *to.toTypedArray()
            )
        )
    }
}