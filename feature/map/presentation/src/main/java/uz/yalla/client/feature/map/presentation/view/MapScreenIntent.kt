package uz.yalla.client.feature.map.presentation.view

import androidx.compose.ui.unit.Dp

sealed interface MapScreenIntent {
    sealed interface MapOverlayIntent : MapScreenIntent {
        data object MoveToMyLocation : MapOverlayIntent
        data object MoveToFirstLocation : MapOverlayIntent
        data object MoveToMyRoute : MapOverlayIntent
        data object ClickShowOrders : MapOverlayIntent
        data object OpenDrawer : MapOverlayIntent
        data object NavigateBack : MapOverlayIntent
    }

    data class SetSheetHeight(val height: Dp) : MapScreenIntent
}