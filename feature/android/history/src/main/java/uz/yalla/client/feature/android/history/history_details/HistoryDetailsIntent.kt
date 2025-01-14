package uz.yalla.client.feature.android.history.history_details

internal sealed interface HistoryDetailsIntent {
    data object NavigateBack : HistoryDetailsIntent
}