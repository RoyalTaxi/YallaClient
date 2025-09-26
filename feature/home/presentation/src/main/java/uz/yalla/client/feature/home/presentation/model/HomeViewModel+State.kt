package uz.yalla.client.feature.home.presentation.model

import uz.yalla.client.core.analytics.event.Event
import uz.yalla.client.core.analytics.event.Logger
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.home.presentation.intent.HomeState
import uz.yalla.client.feature.home.presentation.navigation.OrderSheet

fun HomeViewModel.clearState() = intent {
    Logger.log(Event.AddNewOrderClick)
    reduce {
        state.copy(
            order = null,
            orderId = null,
            location = null,
            destinations = emptyList(),
            route = emptyList(),
            markerState = YallaMarkerState.LOADING
        )
    }
}

fun HomeViewModel.removeLastDestination() = intent {
    if (state.destinations.isNotEmpty()) {
        reduce {
            state.copy(destinations = state.destinations.dropLast(1))
        }
    }
}

fun HomeViewModel.setPermissionDialog(visible: Boolean) = intent {
    reduce {
        state.copy(permissionDialogVisible = visible)
    }
}

fun HomeViewModel.setLocationEnabled(enabled: Boolean) = intent {
    reduce {
        state.copy(locationEnabled = enabled)
    }
}

fun HomeViewModel.setLocationGranted(granted: Boolean) = intent {
    reduce {
        state.copy(locationGranted = granted)
    }
}

fun computeSheet(state: HomeState): OrderSheet? {
    if (state.newServiceAvailability == false) return OrderSheet.NoService

    val order = state.order ?: return OrderSheet.Main

    return when (order.status) {
        OrderStatus.Appointed -> OrderSheet.ClientWaiting(order.id)
        OrderStatus.AtAddress -> OrderSheet.DriverWaiting(order.id)
        OrderStatus.InFetters -> OrderSheet.OnTheRide(order.id)
        OrderStatus.Canceled -> OrderSheet.Canceled(order.id)
        OrderStatus.Completed -> OrderSheet.Feedback(order.id)
        else -> {
            val point = state.location?.point
            val tariffId = state.tariffId
            if (point != null && tariffId != null) {
                OrderSheet.Search(order.id, point, tariffId)
            } else {
                OrderSheet.Main
            }
        }
    }
}