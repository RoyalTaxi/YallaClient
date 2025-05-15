package uz.yalla.client.feature.order.presentation.main.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.sheet.search_address.SearchByNameSheetValue
import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapViewValue
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.PaymentType
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

    val selectedLocationId: Int? = null,
    val selectedLocation: SelectedLocation? = null,
    val destinations: List<Destination> = emptyList(),

    val selectedOptions: List<ServiceModel> = emptyList(),
    val options: List<ServiceModel> = emptyList(),

    val comment: String = "",
    val cardList: List<CardListItemModel> = emptyList(),
    val isBonusEnabled: Boolean = false,
    val bonusAmount: Int = 0,

    val selectedPaymentType: PaymentType = PaymentType.CASH,
    val phoneNumber: String = "",

    val selectedService: String = "road",

    val order: ShowOrderModel? = null,
    val orderId: Int? = null,
    val isSecondaryAddressMandatory: Boolean = false,
    val isShadowVisible: Boolean = false,
    val isPaymentMethodSheetVisible: Boolean = false,
    val isOrderCommentSheetVisible: Boolean = false,
    val isSearchByNameSheetVisible: SearchByNameSheetValue = SearchByNameSheetValue.INVISIBLE,
    val selectFromMapViewVisibility: SelectFromMapViewValue = SelectFromMapViewValue.INVISIBLE,
    val isAddDestinationSheetVisible: Boolean = false,
    val isArrangeDestinationsSheetVisible: Boolean = false,
    val isSetBonusAmountBottomSheetVisible: Boolean = false,
    val isBonusInfoSheetVisibility: Boolean = false
) {

    fun getBadgeText(): String? = when {
        selectedOptions.isNotEmpty() -> selectedOptions.size.toString()
        comment.isNotBlank() -> ""
        else -> null
    }

    fun mapToOrderTaxiDto(): OrderTaxiDto? {
        val addressId = selectedLocationId ?: return null
        val from = selectedLocation ?: return null
        val fromLat = from.point?.lat ?: return null
        val fromLng = from.point?.lng ?: return null

        val toAddresses = destinations
            .mapNotNull { dest ->
                dest.point?.lat?.let { lat ->
                    dest.point?.lng?.let { lng ->
                        OrderTaxiDto.Address(
                            addressId = null,
                            lat = lat,
                            lng = lng,
                            name = dest.name.orEmpty()
                        )
                    }
                }
            }

        val tariff = selectedTariff ?: return null
        val cardId = (selectedPaymentType as? PaymentType.CARD)?.cardId
        val optionsIds = selectedOptions.map { it.id }

        return OrderTaxiDto(
            dontCallMe = false,
            service = selectedService,
            cardId = cardId,
            addressId = addressId,
            toPhone = phoneNumber,
            comment = comment,
            tariffId = tariff.id,
            tariffOptions = optionsIds,
            paymentType = selectedPaymentType.typeName,
            fixedPrice = tariff.fixedType,
            isBonusEnabled = isBonusEnabled,
            bonusAmount = bonusAmount,
            addresses = listOf(
                OrderTaxiDto.Address(
                    addressId = from.addressId,
                    lat = fromLat,
                    lng = fromLng,
                    name = from.name.orEmpty()
                )
            ) + toAddresses
        )
    }
}
