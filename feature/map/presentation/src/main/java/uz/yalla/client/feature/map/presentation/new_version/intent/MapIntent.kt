package uz.yalla.client.feature.map.presentation.new_version.intent

import android.content.Context
import uz.yalla.client.feature.order.domain.model.response.order.ShowOrderModel

sealed interface MapIntent {
    sealed interface MapOverlayIntent : MapIntent {
        data class AnimateToMyLocation(val context: Context) : MapOverlayIntent
        data class MoveToMyLocation(val context: Context) : MapOverlayIntent
        data object MoveToFirstLocation : MapOverlayIntent
        data object MoveToMyRoute : MapOverlayIntent
        data object ClickShowOrders : MapOverlayIntent
        data object OpenDrawer : MapOverlayIntent
        data object NavigateBack : MapOverlayIntent
        data object AskForPermission : MapOverlayIntent
        data object AskForEnable : MapOverlayIntent
        data object OnClickBonus : MapOverlayIntent
        data class RefocusLastState(val context: Context) : MapOverlayIntent
    }

    data object OnDismissActiveOrders : MapIntent
    data class SetShowingOrder(val order: ShowOrderModel) : MapIntent
    data class SetShowingOrderId(val orderId: Int) : MapIntent
}