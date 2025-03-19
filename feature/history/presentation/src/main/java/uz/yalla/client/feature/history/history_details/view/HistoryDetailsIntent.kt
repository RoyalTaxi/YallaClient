package uz.yalla.client.feature.history.history_details.view

internal sealed interface HistoryDetailsIntent {
    data object NavigateBack : HistoryDetailsIntent
}