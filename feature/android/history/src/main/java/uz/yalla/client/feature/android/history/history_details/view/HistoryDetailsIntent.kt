package uz.yalla.client.feature.android.history.history_details.view

internal sealed interface HistoryDetailsIntent {
    data object NavigateBack : HistoryDetailsIntent
}