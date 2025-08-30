package uz.yalla.client.feature.history.history.intent

sealed interface HistorySideEffect {
    data object NavigateBack : HistorySideEffect
    data class NavigateToDetails(val id: Int) : HistorySideEffect
}