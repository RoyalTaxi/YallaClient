package uz.yalla.client.feature.map.presentation.new_version.model

import uz.yalla.client.core.common.maps.MapsIntent
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.map.presentation.new_version.intent.MapEffect
import uz.yalla.client.feature.map.presentation.new_version.intent.MapIntent
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingSheetIntent
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingSheetIntent
import uz.yalla.client.feature.order.presentation.feedback.view.FeedbackSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceSheetIntent
import uz.yalla.client.feature.order.presentation.on_the_ride.view.OnTheRideSheetIntent
import uz.yalla.client.feature.order.presentation.order_canceled.view.OrderCanceledSheetIntent
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetIntent

fun MViewModel.onIntent(intent: MapIntent) = intent {
    when (intent) {
        is MapIntent.MapOverlayIntent.MoveToMyLocation -> {
            mapsViewModel.onIntent(MapsIntent.MoveToMyLocation(intent.context))
        }

        is MapIntent.MapOverlayIntent.AnimateToMyLocation -> {
            mapsViewModel.onIntent(MapsIntent.AnimateToMyLocation(intent.context))
        }

        MapIntent.MapOverlayIntent.AskForEnable -> {
            postSideEffect(MapEffect.EnableLocation)
        }

        MapIntent.MapOverlayIntent.AskForPermission -> {
            postSideEffect(MapEffect.GrantLocation)
        }

        MapIntent.MapOverlayIntent.ClickShowOrders -> {
            reduce { state.copy(ordersSheetVisible = true) }
        }

        MapIntent.MapOverlayIntent.MoveToFirstLocation -> {
            state.location?.point?.let { point ->
                mapsViewModel.onIntent(MapsIntent.AnimateTo(point))
            }
        }

        MapIntent.MapOverlayIntent.MoveToMyRoute -> {
            mapsViewModel.onIntent(MapsIntent.AnimateFitBounds)
        }

        MapIntent.MapOverlayIntent.NavigateBack -> {
            if (state.order == null) removeLastDestination()
            else clearState()
        }

        MapIntent.MapOverlayIntent.OnClickBonus -> {
            MainSheetChannel.setBonusVisibility(true)
        }

        MapIntent.MapOverlayIntent.OpenDrawer -> {
            // This is handled in MapScreen.kt
        }

        MapIntent.OnDismissActiveOrders -> reduce {
            state.copy(ordersSheetVisible = false)
        }

        is MapIntent.SetShowingOrder -> reduce {
            state.copy(
                order = intent.order,
                orderId = intent.order.id,
                ordersSheetVisible = false
            )
        }

        is MapIntent.MapOverlayIntent.RefocusLastState -> {
            if (state.serviceAvailable == false) return@intent
            state.location?.point?.let { point ->
                mapsViewModel.onIntent(MapsIntent.MoveTo(point))
            }
        }
    }
}

fun MViewModel.onIntent(intent: NoServiceSheetIntent) = intent {
    when (intent) {
        is NoServiceSheetIntent.SetSelectedLocation -> {
            reduce {
                state.copy(
                    location = intent.location,
                    markerState = YallaMarkerState.IDLE(intent.location.name, null)
                )
            }
        }
    }
}

fun MViewModel.onIntent(intent: MainSheetIntent) = intent {
    when (intent) {
        is MainSheetIntent.OrderTaxiSheetIntent.SetSelectedLocation -> {
            reduce {
                state.copy(
                    location = intent.location,
                    markerState = YallaMarkerState.IDLE(intent.location.name, null)
                )
            }
        }

        is MainSheetIntent.OrderTaxiSheetIntent.SetDestinations -> {
            reduce { state.copy(destinations = intent.destinations) }
        }

        is MainSheetIntent.OrderTaxiSheetIntent.AddDestination -> {
            reduce { state.copy(destinations = state.destinations + intent.destination) }
        }

        is MainSheetIntent.OrderTaxiSheetIntent.OrderCreated -> {
            reduce {
                state.copy(
                    order = intent.order,
                    orderId = intent.order.id,
                    tariffId = intent.order.taxi.tariffId,
                    markerState = YallaMarkerState.Searching,
                    location = Location(
                        name = intent.order.taxi.routes.firstOrNull()?.fullAddress,
                        addressId = null,
                        point = intent.order.taxi.routes.firstOrNull()?.coords?.let { c ->
                            MapPoint(c.lat, c.lng)
                        }
                    ),
                    destinations = intent.order.taxi.routes.drop(1).map { d ->
                        Destination(
                            name = d.fullAddress,
                            point = MapPoint(d.coords.lat, d.coords.lng)
                        )
                    }
                )
            }
        }

        is MainSheetIntent.OrderTaxiSheetIntent.SetTimeout -> {
            reduce { state.copy(carArrivalInMinutes = intent.timeout) }
        }

        is MainSheetIntent.OrderTaxiSheetIntent.SetServiceState -> {
            reduce { state.copy(serviceAvailable = intent.available) }
        }

        MainSheetIntent.PaymentMethodSheetIntent.OnAddNewCard -> {
            postSideEffect(MapEffect.NavigateToAddCard)
        }

        MainSheetIntent.FooterIntent.Register -> {
            postSideEffect(MapEffect.NavigateToRegister)
        }

        else -> {
            // Other intents are handled on main sheet
        }
    }
}

fun MViewModel.onIntent(intent: SearchCarSheetIntent) = intent {
    when (intent) {
        SearchCarSheetIntent.AddNewOrder -> clearState()
        SearchCarSheetIntent.ZoomOut -> mapsViewModel.onIntent(MapsIntent.ZoomOut)
        is SearchCarSheetIntent.OnCancelled -> {
            val orderId = intent.orderId ?: return@intent
            clearState()
            postSideEffect(MapEffect.NavigateToCancelled(orderId))
        }

        else -> {
            /* no-op */
        }
    }
}

fun MViewModel.onIntent(intent: ClientWaitingSheetIntent) = intent {
    when (intent) {
        ClientWaitingSheetIntent.AddNewOrder -> clearState()
        is ClientWaitingSheetIntent.OnCancelled -> {
            state.orderId?.let { orderId ->
                clearState()
                postSideEffect(MapEffect.NavigateToCancelled(orderId))
            }
        }

        is ClientWaitingSheetIntent.UpdateRoute -> {

        }

        else -> {
            /* no-op */
        }
    }
}

fun MViewModel.onIntent(intent: DriverWaitingSheetIntent) = intent {
    when (intent) {
        DriverWaitingSheetIntent.AddNewOrder -> clearState()
        is DriverWaitingSheetIntent.OnCancelled -> {
            state.orderId?.let { orderId ->
                clearState()
                postSideEffect(MapEffect.NavigateToCancelled(orderId))
            }
        }

        else -> {
            /* no-op */
        }
    }
}

fun MViewModel.onIntent(intent: OnTheRideSheetIntent) = intent {
    when (intent) {
        OnTheRideSheetIntent.AddNewOrder -> clearState()
        else -> {
            /* no-op */
        }
    }
}

fun MViewModel.onIntent(intent: OrderCanceledSheetIntent) {
    when (intent) {
        OrderCanceledSheetIntent.StartNewOrder -> clearState()
    }
}

fun MViewModel.onIntent(intent: FeedbackSheetIntent) {
    when (intent) {
        FeedbackSheetIntent.OnCompleteOrder -> clearState()
        else -> {
            /* no-op */
        }
    }
}
