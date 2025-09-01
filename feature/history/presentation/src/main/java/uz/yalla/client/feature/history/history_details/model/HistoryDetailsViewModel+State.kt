package uz.yalla.client.feature.history.history_details.model

fun HistoryDetailsViewModel.setIsMapReady(isReady: Boolean) = intent {
    reduce {
        state.copy(isMapReady = isReady)
    }
}