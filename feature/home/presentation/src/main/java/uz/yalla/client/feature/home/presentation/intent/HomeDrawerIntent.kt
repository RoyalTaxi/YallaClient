package uz.yalla.client.feature.home.presentation.intent

sealed interface HomeDrawerIntent {
    data object OrdersHistory : HomeDrawerIntent
    data object MyPlaces : HomeDrawerIntent
    data object PaymentType : HomeDrawerIntent
    data class InviteFriend(val title: String, val url: String) : HomeDrawerIntent
    data class BecomeADriver(val title: String, val url: String) : HomeDrawerIntent
    data object ContactUs : HomeDrawerIntent
    data object Settings : HomeDrawerIntent
    data object AboutTheApp : HomeDrawerIntent
    data object Profile : HomeDrawerIntent
    data object Notifications: HomeDrawerIntent
    data object Bonus : HomeDrawerIntent
    data object RegisterDevice : HomeDrawerIntent
}