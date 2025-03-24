package uz.yalla.client.feature.order.presentation.main.view

import androidx.compose.ui.unit.Dp
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel
import uz.yalla.client.core.data.enums.PaymentType
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel

sealed interface MainSheetIntent {
    sealed interface OrderTaxiSheetIntent : MainSheetIntent {
        data object CurrentLocationClick : OrderTaxiSheetIntent
        data object DestinationClick : OrderTaxiSheetIntent
        data object AddNewDestinationClick : OrderTaxiSheetIntent
        data class SetSheetHeight(val height: Dp) : OrderTaxiSheetIntent
        data class OrderCreated(val orderId: Int) : OrderTaxiSheetIntent
        data class SelectTariff(
            val tariff: GetTariffsModel.Tariff,
            val wasSelected: Boolean
        ) : OrderTaxiSheetIntent
    }

    sealed class TariffInfoSheetIntent : MainSheetIntent {
        data object ClickComment : TariffInfoSheetIntent()
        data object ClearOptions : TariffInfoSheetIntent()
        data class ChangeShadowVisibility(val visible: Boolean) : TariffInfoSheetIntent()
        data class OptionsChange(val options: List<GetTariffsModel.Tariff.Service>) :
            TariffInfoSheetIntent()
    }

    sealed interface FooterIntent : MainSheetIntent {
        data object ClickPaymentButton : FooterIntent
        data object CreateOrder : FooterIntent
        data object ClearOptions : FooterIntent
        data class SetFooterHeight(val height: Dp) : FooterIntent
        data class ChangeSheetVisibility(val isExtended: Boolean) : FooterIntent
    }

    data class UpdateActiveOrders(val orders: List<ShowOrderModel>) : MainSheetIntent

    sealed interface PaymentMethodSheetIntent : MainSheetIntent {
        data object OnAddNewCard : PaymentMethodSheetIntent
        data object OnDismissRequest : PaymentMethodSheetIntent
        data class OnSelectPaymentType(val paymentType: PaymentType) : PaymentMethodSheetIntent
    }

    sealed interface OrderCommentSheetIntent : MainSheetIntent {
        data class OnDismissRequest(val comment: String) : OrderCommentSheetIntent
    }
}