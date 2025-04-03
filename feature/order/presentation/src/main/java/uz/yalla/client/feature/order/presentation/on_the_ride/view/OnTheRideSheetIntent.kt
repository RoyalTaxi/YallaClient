package uz.yalla.client.feature.order.presentation.on_the_ride.view

sealed interface OnTheRideSheetIntent {
    data object AddNewOrder : OnTheRideSheetIntent
}