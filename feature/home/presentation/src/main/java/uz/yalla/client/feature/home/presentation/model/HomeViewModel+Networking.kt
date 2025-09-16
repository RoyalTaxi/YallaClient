package uz.yalla.client.feature.home.presentation.model

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.home.domain.model.request.GetRoutingDtoItem
import uz.yalla.client.feature.home.presentation.intent.HomeEffect
import kotlin.math.ceil


fun HomeViewModel.getMe() = viewModelScope.launch {
    getMeUseCase().onSuccess { user ->
        intent { reduce { state.copy(client = user.client, balance = user.client.balance) } }
        prefs.setBalance(user.client.balance)
    }.onFailure(::handleException)
}


fun HomeViewModel.getNotificationsCount() = viewModelScope.launch {
    getNotificationsCountUseCase().onSuccess { count ->
        intent { reduce { state.copy(notificationCount = count) } }
    }
}

fun HomeViewModel.getAddress(point: MapPoint) = viewModelScope.launch {
    getAddressNameUseCase(point.lat, point.lng).onSuccess { address ->
        intent {
            if (state.destinations.isEmpty() && state.order == null) {
                reduce {
                    state.copy(
                        location = Location(
                            name = address.displayName,
                            addressId = null,
                            point = MapPoint(point.lat, point.lng)
                        ),
                        markerState = YallaMarkerState.IDLE(
                            address.displayName,
                            state.carArrivalInMinutes
                        )
                    )
                }
            }
        }
    }
}

fun HomeViewModel.getActiveOrders() = viewModelScope.launch {
    getActiveOrdersUseCase().onSuccess { activeOrders ->
        val shouldInject = activeOrders.list.size == 1 && !staticPreferences.hasInjectedOrderOnEntry

        if (shouldInject) intent {
            val order = activeOrders.list.first()
            reduce { state.copy(order = order, orderId = order.id) }
            staticPreferences.hasInjectedOrderOnEntry = true
        } else if (activeOrders.list.size > 1 && !staticPreferences.hasInjectedOrderOnEntry) intent {
            postSideEffect(HomeEffect.ActiveOrderSheetState(visible = true))
            reduce { state.copy(ordersSheetVisible = true) }
            staticPreferences.hasInjectedOrderOnEntry = true
        }

        intent { reduce { state.copy(orders = activeOrders.list) } }
    }
}

fun HomeViewModel.getSettingConfig() = viewModelScope.launch {
    getSettingUseCase().onSuccess { setting ->
        prefs.setBonusEnabled(setting.isBonusEnabled)
        prefs.setMinBonus(setting.minBonus)
        prefs.setMaxBonus(setting.maxBonus)
    }
}

fun HomeViewModel.getRouting() = intent {
    val points = if (state.order?.status == OrderStatus.Appointed) listOfNotNull(
        state.order?.executor?.coords?.let { MapPoint(it.lat, it.lng) },
        state.location?.point,
    ) else listOfNotNull(
        state.location?.point,
        *state.destinations.mapNotNull { it.point }.toTypedArray()
    )

    if (points.size < 2) {
        intent { reduce { state.copy(route = emptyList()) } }
        return@intent
    }

    val addresses = mutableListOf<GetRoutingDtoItem>()

    addresses.add(
        GetRoutingDtoItem(
            type = GetRoutingDtoItem.START,
            lat = points.first().lat,
            lng = points.first().lng
        )
    )

    for (i in 1 until points.lastIndex) {
        addresses.add(
            GetRoutingDtoItem(
                type = GetRoutingDtoItem.POINT,
                lat = points[i].lat,
                lng = points[i].lng
            )
        )
    }

    addresses.add(
        GetRoutingDtoItem(
            type = GetRoutingDtoItem.STOP,
            lat = points.last().lat,
            lng = points.last().lng
        )
    )

    viewModelScope.launch {
        getRoutingUseCase(addresses).onSuccess { route ->
            val duration = route.duration
                .takeIf { it != 0.0 }
                ?.div(60)

            val routingPoints = route.routing.map { MapPoint(it.lat, it.lng) }

            intent {
                reduce {
                    state.copy(
                        orderEndsInMinutes = duration?.let { ceil(it).toInt() },
                        route = routingPoints
                    )
                }
            }
        }
    }
}

fun HomeViewModel.getActiveOrder() = viewModelScope.launch {
    val orderId = container.stateFlow.value.orderId ?: return@launch
    getShowOrderUseCase(orderId).onSuccess { order ->
        intent {
            reduce {
                state.copy(
                    order = order,
                    tariffId = order.taxi.tariffId,
                    markerState = YallaMarkerState.Searching,
                    drivers = state.drivers
                        .takeIf { order.status in OrderStatus.nonInteractive }
                        .orEmpty(),
                    location = Location(
                        name = order.taxi.routes.firstOrNull()?.fullAddress,
                        addressId = null,
                        point = order.taxi.routes.firstOrNull()?.coords?.let { c ->
                            MapPoint(c.lat, c.lng)
                        }
                    ),
                    destinations = order.taxi.routes.drop(1).map { d ->
                        Destination(
                            name = d.fullAddress,
                            point = MapPoint(d.coords.lat, d.coords.lng)
                        )
                    }
                )
            }
        }
    }
}
