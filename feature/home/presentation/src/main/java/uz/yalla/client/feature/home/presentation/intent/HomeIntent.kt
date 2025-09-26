package uz.yalla.client.feature.home.presentation.intent

import android.content.Context
import androidx.compose.ui.unit.Dp
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

sealed interface HomeIntent {
    sealed interface HomeOverlayIntent : HomeIntent {
        data class AnimateToMyLocation(val context: Context) : HomeOverlayIntent
        data object AnimateToFirstLocation : HomeOverlayIntent
        data object AnimateToMyRoute : HomeOverlayIntent
        data object ClickShowOrders : HomeOverlayIntent
        data object OpenDrawer : HomeOverlayIntent
        data object CloseDrawer : HomeOverlayIntent
        data object NavigateBack : HomeOverlayIntent
        data object AskForPermission : HomeOverlayIntent
        data object AskForEnable : HomeOverlayIntent
        data object OnClickBonus : HomeOverlayIntent
    }

    data class SetShowingOrder(val order: ShowOrderModel) : HomeIntent
    data class SetShowingOrderId(val orderId: Int) : HomeIntent
    data object OnDismissActiveOrders : HomeIntent


    data class SetTopPadding(val topPadding: Dp) : HomeIntent
}
