package uz.yalla.client.feature.home.presentation.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import uz.yalla.client.feature.home.presentation.intent.HomeIntent
import uz.yalla.client.feature.home.presentation.navigation.OrderSheet
import uz.yalla.client.feature.order.presentation.cancel_reason.view.CancelReasonSheet
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingSheet
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingSheet
import uz.yalla.client.feature.order.presentation.feedback.view.FeedbackSheet
import uz.yalla.client.feature.order.presentation.main.view.MainSheet
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceSheet
import uz.yalla.client.feature.order.presentation.on_the_ride.view.OnTheRideSheet
import uz.yalla.client.feature.order.presentation.order_canceled.view.OrderCanceledSheet
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheet

@Composable
fun OrderSheetHost(
    sheet: OrderSheet?,
    isServiceAvailable: Boolean,
    wasServiceAvailable: Boolean,
    hasActiveOrder: Boolean,
    onIntent: (HomeIntent) -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { }
    AnimatedContent(
        targetState = sheet,
        label = "order_sheet",
        modifier = Modifier
            .onGloballyPositioned {
                if ((isServiceAvailable && wasServiceAvailable) || hasActiveOrder)
                    onIntent(HomeIntent.HomeOverlayIntent.RefocusLastState(context))
            }
            .onSizeChanged {
                if ((isServiceAvailable && wasServiceAvailable) || hasActiveOrder)
                    onIntent(HomeIntent.HomeOverlayIntent.RefocusLastState(context))
            }
    ) { current ->
        when (current) {
            null -> {}
            is OrderSheet.Main -> MainSheet()
            is OrderSheet.Search -> SearchCarSheet(
                point = current.point,
                orderId = current.orderId,
                tariffId = current.tariffId
            )

            is OrderSheet.ClientWaiting -> ClientWaitingSheet(orderId = current.orderId)
            is OrderSheet.DriverWaiting -> DriverWaitingSheet(orderId = current.orderId)
            is OrderSheet.OnTheRide -> OnTheRideSheet(orderId = current.orderId)
            is OrderSheet.Canceled -> OrderCanceledSheet()
            is OrderSheet.Feedback -> FeedbackSheet(orderID = current.orderId)
            is OrderSheet.NoService -> NoServiceSheet()
            is OrderSheet.CancelReason -> CancelReasonSheet(orderId = current.orderId)
        }
    }
}
