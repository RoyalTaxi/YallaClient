package uz.yalla.client.feature.order.presentation.on_the_ride.view

import androidx.compose.ui.unit.Dp

sealed interface OnTheRideSheetIntent {
    data object AddNewOrder : OnTheRideSheetIntent
    data class SetHeaderHeight(val height: Dp) : OnTheRideSheetIntent
}