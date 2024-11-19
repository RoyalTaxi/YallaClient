package uz.ildam.technologies.yalla.android.ui.screens.history

sealed interface HistoryIntent {
    data object OnNavigateBack : HistoryIntent
    data class OnHistoryItemClick(val id: Int) : HistoryIntent
}