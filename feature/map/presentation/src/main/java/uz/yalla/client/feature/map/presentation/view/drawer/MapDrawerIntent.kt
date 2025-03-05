package uz.yalla.client.feature.map.presentation.view.drawer

sealed interface MapDrawerIntent {
    data object OrdersHistory : MapDrawerIntent
    data object MyPlaces : MapDrawerIntent
    data object PaymentType : MapDrawerIntent
    data class InviteFriend(val title: String, val url: String) : MapDrawerIntent
    data class BecomeADriver(val title: String, val url: String) : MapDrawerIntent
    data object ContactUs : MapDrawerIntent
    data object Settings : MapDrawerIntent
    data object AboutTheApp : MapDrawerIntent
    data object Profile : MapDrawerIntent
}