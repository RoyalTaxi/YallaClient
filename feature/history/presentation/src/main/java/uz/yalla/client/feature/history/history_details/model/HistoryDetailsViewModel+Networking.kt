package uz.yalla.client.feature.history.history_details.model

import androidx.lifecycle.viewModelScope

fun HistoryDetailsViewModel.getOrderHistory(orderId: Int) = intent {
    viewModelScope.launchWithLoading {
        getOrderHistoryUseCase(orderId)
            .onSuccess { result ->
                reduce {
                    state.copy(orderDetails = result)
                }
                getMapPoints()
            }
            .onFailure(::handleException)
    }
}

fun HistoryDetailsViewModel.getMapPoints() = intent {
    viewModelScope.launchWithLoading {
        getTariffsUseCase(
            coords = state.orderDetails?.taxi?.routes?.map {
                Pair(
                    it.cords.lat,
                    it.cords.lng
                )
            }.orEmpty(),
            optionIds = emptyList()
        ).onSuccess { result ->
            reduce {
                state.copy(routes = result.map.routing)
            }
        }.onFailure(::handleException)
    }
}