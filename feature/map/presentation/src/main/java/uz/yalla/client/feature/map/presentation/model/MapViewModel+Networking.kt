package uz.yalla.client.feature.map.presentation.model

import kotlinx.coroutines.flow.first
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.map.domain.model.request.GetRoutingDtoItem
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import kotlin.math.ceil

suspend fun MViewModel.getMe() {
    getMeUseCase().onSuccess { user ->
        updateState { state -> state.copy(client = user.client, balance = user.client.balance) }
        prefs.setBalance(user.client.balance)
    }.onFailure(::handleException)
}


suspend fun MViewModel.getNotificationsCount() {
    getNotificationsCountUseCase().onSuccess { count ->
        updateState { state -> state.copy(notificationCount = count) }
    }
}

suspend fun MViewModel.getAddress(point: MapPoint) {
    getAddressNameUseCase(point.lat, point.lng).onSuccess { address ->
        if (stateFlow.value.destinations.isEmpty() && stateFlow.value.order == null) {
            val location = Location(
                name = address.displayName,
                addressId = null,
                point = MapPoint(point.lat, point.lng)
            )
            MainSheetChannel.setLocation(location = location)
            updateState { state ->
                state.copy(
                    markerState = YallaMarkerState.IDLE(
                        address.displayName,
                        state.carArrivalInMinutes
                    ),
                    location = Location(
                        name = address.displayName,
                        addressId = null,
                        point = MapPoint(point.lat, point.lng)
                    )
                )
            }
        }
    }
}

suspend fun MViewModel.getActiveOrders() {
    val alreadyMarkedAsProcessed = prefs.hasProcessedOrderOnEntry.first()

    getActiveOrdersUseCase().onSuccess { activeOrders ->
        val shouldInject = activeOrders.list.size == 1 && !alreadyMarkedAsProcessed

        if (shouldInject) {
            val order = activeOrders.list.first()
            updateState { state -> state.copy(order = order, orderId = order.id) }
            prefs.setHasProcessedOrderOnEntry(true)
        } else if (activeOrders.list.size > 1 && !alreadyMarkedAsProcessed) {
            updateState { state -> state.copy(ordersSheetVisible = true) }
            prefs.setHasProcessedOrderOnEntry(true)
        }

        updateState { state -> state.copy(orders = activeOrders.list) }
    }
}

suspend fun MViewModel.getSettingConfig() {
    getSettingUseCase().onSuccess { setting ->
        prefs.setBonusEnabled(setting.isBonusEnabled)
        prefs.setMinBonus(setting.minBonus)
        prefs.setMaxBonus(setting.maxBonus)
    }
}

suspend fun MViewModel.getRouting() {
    val points = listOfNotNull(
        stateFlow.value.location?.point,
        *stateFlow.value.destinations.mapNotNull { it.point }.toTypedArray()
    )

    if (points.size < 2) {
        updateState { state -> state.copy(route = emptyList()) }
        return
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

    getRoutingUseCase(addresses).onSuccess { route ->
        // Drop stale result if points changed meanwhile
        val currentPoints = listOfNotNull(
            stateFlow.value.location?.point,
            *stateFlow.value.destinations.mapNotNull { it.point }.toTypedArray()
        )
        if (currentPoints != points) return@onSuccess

        val duration = route.duration
            .takeIf { it != 0.0 }
            ?.div(60)

        val routingPoints = route.routing.map { MapPoint(it.lat, it.lng) }

        updateState { state ->
            state.copy(
                orderEndsInMinutes = duration?.let { ceil(it).toInt() },
                route = routingPoints
            )
        }
    }
}

suspend fun MViewModel.getActiveOrder() {
    val requestedOrderId = stateFlow.value.orderId ?: return
    getShowOrderUseCase(requestedOrderId).onSuccess { order ->
        // Ignore stale response if orderId was cleared/changed
        if (stateFlow.value.orderId != requestedOrderId) return@onSuccess

        updateState { state ->
            state.copy(
                order = order,
                tariffId = order.taxi.tariffId,
                markerState = YallaMarkerState.Searching,
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
