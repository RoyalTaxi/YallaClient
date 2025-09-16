package uz.yalla.client.feature.history.history_details.intent

sealed interface HistoryDetailsIntent {
    data object NavigateBack : HistoryDetailsIntent
}