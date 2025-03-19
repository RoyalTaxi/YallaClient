package uz.yalla.client.feature.payment.top_up_balance.view

internal sealed interface TopUpIntent {
    data object OnNavigateBack: TopUpIntent
    data class SetValue(val value: String): TopUpIntent
}