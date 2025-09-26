package uz.yalla.client.core.analytics.event

sealed class Event(
    val name: String
) {
    internal companion object {
        const val SOURCE = "from"
        const val OPTION = "option"
    }

    data object OverlayBonusClick : Event(name = "overlay_bonus_click")
    data object PaymentMethodBonusClick : Event(name = "payment_method_bonus_click")
    data class ActivateBonusClick(val source: Event) : Event(name = "activate_bonus_click")

    data object OrderHistoryClick : Event("drawer_order_history_click")
    data object MyPlacesClick : Event("drawer_my_places_click")
    data object BonusAndDiscountsClick : Event("drawer_bonus_and_discounts_click")
    data object PaymentTypeClick : Event("drawer_payment_type_click")
    data object InviteFriendsClick : Event("drawer_invite_friends_click")
    data object BecomeDriverClick : Event("drawer_become_driver_click")
    data object ContactUsClick : Event("drawer_contact_us_click")
    data object NotificationsClick : Event("drawer_notifications_click")
    data object SettingsClick : Event("drawer_settings_click")
    data object AboutAppClick : Event("drawer_about_app_click")

    data object SecondaryAddressOptionClick : Event("secondary_address_option_click")
    data object CommentClick : Event("comment_click")

    data class ServiceOptionClick(val option: String) : Event("service_option_click")
    data class TariffOptionClick(val tariff: String) : Event("tariff_option_click")

    data object CreateOrderClick : Event("create_order_click")
    data object OrderCreated : Event("order_created")
    data object CancelOrderClick : Event("cancel_order_click")
    data object OrderCancelled : Event("order_cancelled")
    data object AddNewOrderClick : Event("add_new_order_click")
}
