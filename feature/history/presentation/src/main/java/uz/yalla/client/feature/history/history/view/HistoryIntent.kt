package uz.yalla.client.feature.history.history.view

 sealed interface HistoryIntent {
    data object OnNavigateBack : HistoryIntent
    data class OnHistoryItemClick(val id: Int) : HistoryIntent
}