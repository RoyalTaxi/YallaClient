package uz.yalla.client.feature.map.presentation.new_version.model

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.maps.MapsIntent
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.map.domain.model.request.GetRoutingDtoItem
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import kotlin.math.ceil

fun MViewModel.getMe() = viewModelScope.launch {
    getMeUseCase().onSuccess { user ->
        updateState { state -> state.copy(client = user.client, balance = user.client.balance) }
        prefs.setBalance(user.client.balance)
    }.onFailure(::handleException)
}


fun MViewModel.getNotificationsCount() = viewModelScope.launch {
    getNotificationsCountUseCase().onSuccess { count ->
        updateState { state -> state.copy(notificationCount = count) }
    }
}

fun MViewModel.getAddress(point: MapPoint) = viewModelScope.launch {
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

fun MViewModel.getActiveOrders() {
    viewModelScope.launch {
        val alreadyMarkedAsProcessed = staticPrefs.hasProcessedOrderOnEntry

        getActiveOrdersUseCase().onSuccess { activeOrders ->
            val shouldInject = activeOrders.list.size == 1 &&
                    !alreadyMarkedAsProcessed &&
                    !hasInjectedOnceInThisSession

            if (shouldInject) {
                val order = activeOrders.list.first()
                updateState { state ->
                    state.copy(
                        order = order,
                        orderId = order.id
                    )
                }
                staticPrefs.hasProcessedOrderOnEntry = true
                hasInjectedOnceInThisSession = true
                getActiveOrder()
            } else if (activeOrders.list.size > 1 && !alreadyMarkedAsProcessed) {
                updateState { state -> state.copy(ordersSheetVisible = true) }
                staticPrefs.hasProcessedOrderOnEntry = true
                hasInjectedOnceInThisSession = true
            }

            updateState { state -> state.copy(orders = activeOrders.list) }
        }
    }
}

fun MViewModel.getSettingConfig() {
    viewModelScope.launch {
        getSettingUseCase().onSuccess { setting ->
            prefs.setBonusEnabled(setting.isBonusEnabled)
            prefs.setMinBonus(setting.minBonus)
            prefs.setMaxBonus(setting.maxBonus)
        }
    }
}

fun MViewModel.getRouting() {
    val points = listOfNotNull(
        stateFlow.value.location?.point,
        *stateFlow.value.destinations.mapNotNull { it.point }.toTypedArray()
    )

    mapsViewModel.onIntent(MapsIntent.UpdateLocations(points))

    if (points.size < 2) {
        updateState { state -> state.copy(route = emptyList()) }
        return
    }

    viewModelScope.launch {
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
                type = GetRoutingDtoItem.END,
                lat = points.last().lat,
                lng = points.last().lng
            )
        )

        getRoutingUseCase(addresses).onSuccess { route ->
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
}


fun MViewModel.getActiveOrder() = viewModelScope.launch {
    val orderId = stateFlow.value.orderId ?: run {
        mapsViewModel.onIntent(MapsIntent.UpdateOrderStatus(null))
        return@launch
    }
    getShowOrderUseCase(orderId).onSuccess { order ->
        staticPrefs.processingOrderId = order.id
        mapsViewModel.onIntent(MapsIntent.UpdateOrderStatus(order))
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
    }.onFailure {
        staticPrefs.processingOrderId = null
    }
}
