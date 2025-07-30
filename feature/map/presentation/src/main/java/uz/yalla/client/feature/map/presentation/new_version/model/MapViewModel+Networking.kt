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
    getMeUseCase().onSuccess {
        intent { reduce { state.copy(client = it.client, balance = it.client.balance) } }
        prefs.setBalance(it.client.balance)
    }.onFailure(::handleException)
}


fun MViewModel.getNotificationsCount() = viewModelScope.launch {
    getNotificationsCountUseCase().onSuccess {
        intent { reduce { state.copy(notificationCount = it) } }
    }
}

fun MViewModel.getAddress(point: MapPoint) = viewModelScope.launch {
    getAddressNameUseCase(point.lat, point.lng).onSuccess {
        intent {
            if (state.destinations.isEmpty() && state.order == null) {
                val location = Location(
                    name = it.displayName,
                    addressId = null,
                    point = MapPoint(point.lat, point.lng)
                )
                MainSheetChannel.setLocation(location = location)
                reduce {
                    state.copy(
                        markerState = YallaMarkerState.IDLE(
                            it.displayName,
                            state.carArrivalInMinutes
                        ),
                        location = Location(
                            name = it.displayName,
                            addressId = null,
                            point = MapPoint(point.lat, point.lng)
                        )
                    )
                }
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
                intent {
                    reduce {
                        state.copy(
                            order = order,
                            orderId = order.id
                        )
                    }
                }
                staticPrefs.hasProcessedOrderOnEntry = true
                hasInjectedOnceInThisSession = true
                getActiveOrder()
            } else if (activeOrders.list.size > 1 && !alreadyMarkedAsProcessed) {
                intent { reduce { state.copy(ordersSheetVisible = true) } }
                staticPrefs.hasProcessedOrderOnEntry = true
                hasInjectedOnceInThisSession = true
            }

            intent { reduce { state.copy(orders = activeOrders.list) } }
        }
    }
}

fun MViewModel.getSettingConfig() {
    viewModelScope.launch {
        getSettingUseCase().onSuccess {
            prefs.setBonusEnabled(it.isBonusEnabled)
            prefs.setMinBonus(it.minBonus)
            prefs.setMaxBonus(it.maxBonus)
        }
    }
}

fun MViewModel.getRouting() = intent {
    val points = listOfNotNull(
        state.location?.point,
        *state.destinations.mapNotNull { it.point }.toTypedArray()
    )

    if (points.size < 2) {
        reduce { state.copy(route = emptyList()) }
        return@intent
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

            reduce {
                state.copy(
                    orderEndsInMinutes = duration?.let { ceil(it).toInt() },
                    route = routingPoints
                )
            }
        }
    }
}


fun MViewModel.getActiveOrder() = viewModelScope.launch {
    intent {
        val orderId = state.orderId ?: return@intent
        getShowOrderUseCase(orderId).onSuccess { order ->
            mapsViewModel.onIntent(MapsIntent.UpdateOrderStatus(order))
            reduce {
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
}
