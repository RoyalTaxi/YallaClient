package uz.ildam.technologies.yalla.android.ui.screens.map

sealed interface MapDrawerIntent {
    data object OrdersHistory : MapDrawerIntent
    data object MyPlaces : MapDrawerIntent
    data object BonusesAndDiscounts : MapDrawerIntent
    data object PaymentType : MapDrawerIntent
    data object InviteFriend : MapDrawerIntent
    data object BecomeADriver : MapDrawerIntent
    data object ContactUs : MapDrawerIntent
    data object Settings : MapDrawerIntent
    data object AboutTheApp : MapDrawerIntent
}