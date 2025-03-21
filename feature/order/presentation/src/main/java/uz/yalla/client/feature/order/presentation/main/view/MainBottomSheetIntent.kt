package uz.yalla.client.feature.order.presentation.main.view

import androidx.compose.ui.unit.Dp
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel

sealed interface MainBottomSheetIntent {
    sealed interface OrderTaxiBottomSheetIntent : MainBottomSheetIntent {
        data object CurrentLocationClick : OrderTaxiBottomSheetIntent
        data object DestinationClick : OrderTaxiBottomSheetIntent
        data object AddNewDestinationClick : OrderTaxiBottomSheetIntent
        data class SetSheetHeight(val height: Dp) : OrderTaxiBottomSheetIntent
        data class OrderCreated(val orderId: Int) : OrderTaxiBottomSheetIntent
        data class SelectTariff(
            val tariff: GetTariffsModel.Tariff,
            val wasSelected: Boolean
        ) : OrderTaxiBottomSheetIntent
    }

    sealed class TariffInfoBottomSheetIntent : MainBottomSheetIntent {
        data object ClickComment : TariffInfoBottomSheetIntent()
        data object ClearOptions : TariffInfoBottomSheetIntent()
        data class OptionsChange(val options: List<GetTariffsModel.Tariff.Service>) : TariffInfoBottomSheetIntent()
    }

    sealed interface FooterIntent : MainBottomSheetIntent {
        data object ClickPaymentButton : FooterIntent
        data object CreateOrder : FooterIntent
        data object ClearOptions : FooterIntent
        data class SetFooterHeight(val height: Dp) : FooterIntent
        data class ChangeSheetVisibility(val isExtended: Boolean) : FooterIntent
    }
}