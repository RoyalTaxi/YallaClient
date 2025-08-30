package uz.yalla.client.feature.history.history.model

import uz.yalla.client.feature.history.history.intent.HistoryIntent
import uz.yalla.client.feature.history.history.intent.HistorySideEffect

fun HistoryViewModel.onIntent(intent: HistoryIntent) = intent {
    when (intent) {
        is HistoryIntent.OnHistoryItemClick -> postSideEffect(
            HistorySideEffect.NavigateToDetails(id = intent.id)
        )
        HistoryIntent.OnNavigateBack -> postSideEffect(HistorySideEffect.NavigateBack)
    }
}