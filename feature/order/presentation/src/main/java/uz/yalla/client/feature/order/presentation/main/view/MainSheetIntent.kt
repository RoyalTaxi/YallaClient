package uz.yalla.client.feature.order.presentation.main.view

import androidx.compose.ui.unit.Dp
import uz.yalla.client.core.data.enums.PaymentType
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Executor
import uz.yalla.client.core.domain.model.SelectedLocation
import uz.yalla.client.core.domain.model.ServiceModel
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel

sealed interface MainSheetIntent {
    sealed interface OrderTaxiSheetIntent : MainSheetIntent {
        data object CurrentLocationClick : OrderTaxiSheetIntent
        data object DestinationClick : OrderTaxiSheetIntent
        data object AddNewDestinationClick : OrderTaxiSheetIntent
        data class SetSheetHeight(val height: Dp) : OrderTaxiSheetIntent
        data class OrderCreated(val orderId: Int) : OrderTaxiSheetIntent
        data class SetSelectedLocation(val selectedLocation: SelectedLocation) :
            OrderTaxiSheetIntent

        data class SetDestinations(val destinations: List<Destination>) : OrderTaxiSheetIntent
        data class AddDestination(val destination: Destination) : OrderTaxiSheetIntent
        data class SetServiceState(val available: Boolean) : OrderTaxiSheetIntent

        data class SetTimeout(
            val timeout: Int?,
            val drivers: List<Executor>
        ) : OrderTaxiSheetIntent

        data class SelectTariff(
            val tariff: GetTariffsModel.Tariff,
            val wasSelected: Boolean
        ) : OrderTaxiSheetIntent
    }

    sealed class TariffInfoSheetIntent : MainSheetIntent {
        data object ClickComment : TariffInfoSheetIntent()
        data object ClearOptions : TariffInfoSheetIntent()
        data class ChangeShadowVisibility(val visible: Boolean) : TariffInfoSheetIntent()
        data class OptionsChange(val options: List<ServiceModel>) :
            TariffInfoSheetIntent()
    }

    sealed interface FooterIntent : MainSheetIntent {
        data object ClickPaymentButton : FooterIntent
        data object CreateOrder : FooterIntent
        data object ClearOptions : FooterIntent
        data class SetFooterHeight(val height: Dp) : FooterIntent
        data class ChangeSheetVisibility(val isExtended: Boolean) : FooterIntent
    }

    sealed interface PaymentMethodSheetIntent : MainSheetIntent {
        data object OnAddNewCard : PaymentMethodSheetIntent
        data object OnDismissRequest : PaymentMethodSheetIntent
        data class OnSelectPaymentType(val paymentType: PaymentType) : PaymentMethodSheetIntent
    }

    sealed interface OrderCommentSheetIntent : MainSheetIntent {
        data class OnDismissRequest(val comment: String) : OrderCommentSheetIntent
    }
}