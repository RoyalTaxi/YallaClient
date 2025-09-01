package uz.yalla.client.feature.history.history_details.model

import uz.yalla.client.feature.history.history_details.intent.HistoryDetailsIntent
import uz.yalla.client.feature.history.history_details.intent.HistoryDetailsSideEffect

fun HistoryDetailsViewModel.onIntent(intent: HistoryDetailsIntent) = intent {
    when (intent) {
        HistoryDetailsIntent.NavigateBack -> postSideEffect(HistoryDetailsSideEffect.NavigateBack)
        HistoryDetailsIntent.OnMapReady -> {
            postSideEffect(HistoryDetailsSideEffect.UpdateRoute)
            setIsMapReady(true)
        }
    }
}