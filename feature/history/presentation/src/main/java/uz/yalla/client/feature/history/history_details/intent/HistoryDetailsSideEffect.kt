package uz.yalla.client.feature.history.history_details.intent

sealed interface HistoryDetailsSideEffect {
    data object NavigateBack: HistoryDetailsSideEffect
    data object UpdateRoute: HistoryDetailsSideEffect
}