package uz.yalla.client.feature.history.history_details.model

import androidx.lifecycle.viewModelScope
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.home.domain.model.request.GetRoutingDtoItem

fun HistoryDetailsViewModel.getOrderHistory(orderId: Int) = intent {
    viewModelScope.launchWithLoading {
        getOrderHistoryUseCase(orderId)
            .onSuccess { result ->
                reduce { state.copy(orderDetails = result) }
                getRouting()
            }
            .onFailure(::handleException)
    }
}

fun HistoryDetailsViewModel.getRouting() = intent {
    viewModelScope.launchWithLoading {
        val locations = mutableListOf<MapPoint>()
        val getRoutingPoints = mutableListOf<GetRoutingDtoItem>()

        state.orderDetails?.taxi?.routes?.takeIf { it.isNotEmpty() }?.let { routes ->
            locations.add(
                MapPoint(
                    lat = routes.first().cords.lat,
                    lng = routes.first().cords.lng
                )
            )
            getRoutingPoints.add(
                GetRoutingDtoItem(
                    type = GetRoutingDtoItem.START,
                    lat = routes.first().cords.lat,
                    lng = routes.first().cords.lng
                )
            )
        }

        state.orderDetails?.taxi?.routes?.takeIf { it.size > 2 }?.let { routes ->
            locations.addAll(
                routes.drop(1).dropLast(1).map {
                    MapPoint(
                        lat = it.cords.lat,
                        lng = it.cords.lng
                    )
                }
            )
            getRoutingPoints.addAll(
                routes.drop(1).dropLast(1).map {
                    GetRoutingDtoItem(
                        type = GetRoutingDtoItem.POINT,
                        lat = it.cords.lat,
                        lng = it.cords.lng
                    )
                }
            )
        }

        state.orderDetails?.taxi?.routes?.takeIf { it.size > 1 }?.let { routes ->
            locations.add(
                MapPoint(
                    lat = routes.last().cords.lat,
                    lng = routes.last().cords.lng
                )
            )
            getRoutingPoints.add(
                GetRoutingDtoItem(
                    type = GetRoutingDtoItem.STOP,
                    lat = routes.last().cords.lat,
                    lng = routes.last().cords.lng
                )
            )
        }

        reduce { state.copy(locations = locations) }
        getRoutingUseCase(getRoutingPoints).onSuccess { data ->
            reduce { state.copy(route = data.routing.map { MapPoint(it.lat, it.lng) }) }
        }
    }
}