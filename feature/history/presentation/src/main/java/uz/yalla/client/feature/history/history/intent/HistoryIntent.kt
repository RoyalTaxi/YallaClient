package uz.yalla.client.feature.history.history.intent

 sealed interface HistoryIntent {
    data object OnNavigateBack : HistoryIntent
    data class OnHistoryItemClick(val id: Int) : HistoryIntent
}