package uz.yalla.client.feature.android.payment.top_up_balance.view

internal sealed interface TopUpIntent {
    data object OnNavigateBack: TopUpIntent
    data class SetValue(val value: String): TopUpIntent
}