package uz.yalla.client.feature.history.history_details.view

 sealed interface HistoryDetailsIntent {
    data object NavigateBack : HistoryDetailsIntent
    data object OnMapReady : HistoryDetailsIntent
}