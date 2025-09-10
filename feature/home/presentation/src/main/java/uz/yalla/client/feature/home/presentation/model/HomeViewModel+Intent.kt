package uz.yalla.client.feature.home.presentation.model

import uz.yalla.client.core.common.map.core.intent.MapIntent.*
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.home.presentation.intent.HomeEffect.*
import uz.yalla.client.feature.home.presentation.intent.HomeIntent
import uz.yalla.client.feature.home.presentation.intent.HomeIntent.*
import uz.yalla.client.feature.order.presentation.cancel_reason.view.CancelReasonIntent
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingSheetIntent
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingSheetIntent
import uz.yalla.client.feature.order.presentation.feedback.view.FeedbackSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceSheetIntent
import uz.yalla.client.feature.order.presentation.on_the_ride.view.OnTheRideSheetIntent
import uz.yalla.client.feature.order.presentation.order_canceled.view.OrderCanceledSheetIntent
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetIntent

fun HomeViewModel.onIntent(intent: HomeIntent) = intent {
    when (intent) {
        is HomeOverlayIntent.AnimateToMyLocation -> {
            mapsViewModel.onIntent(AnimateToMyLocation)
        }

        HomeOverlayIntent.AskForEnable -> {
            postSideEffect(EnableLocation)
        }

        HomeOverlayIntent.AskForPermission -> {
            postSideEffect(GrantLocation)
        }

        HomeOverlayIntent.ClickShowOrders -> {
            postSideEffect(ActiveOrderSheetState(visible = true))
            reduce { state.copy(ordersSheetVisible = true) }
        }

        HomeOverlayIntent.AnimateToFirstLocation -> {
            mapsViewModel.onIntent(AnimateToFirstLocation)
        }

        HomeOverlayIntent.AnimateToMyRoute -> {
            mapsViewModel.onIntent(AnimateToRoute)
        }

        HomeOverlayIntent.NavigateBack -> {
            if (state.order == null) removeLastDestination()
            else clearState()
        }

        HomeOverlayIntent.OnClickBonus -> {
            MainSheetChannel.setBonusVisibility(true)
        }

        HomeOverlayIntent.OpenDrawer -> {
            // This is handled in HomeScreen.kt
        }

        is SetShowingOrder -> {
            reduce {
                state.copy(
                    order = intent.order,
                    orderId = intent.order.id,
                    ordersSheetVisible = false
                )
            }
        }

        is HomeOverlayIntent.RefocusLastState -> {
            refocus()
        }

        is SetShowingOrderId -> {
            reduce { state.copy(orderId = intent.orderId) }
        }

        is SetTopPadding -> {
            reduce { state.copy(topPadding = intent.topPadding) }
        }

        OnDismissActiveOrders -> {
            reduce { state.copy(ordersSheetVisible = false) }
        }
    }
}

fun HomeViewModel.onIntent(intent: NoServiceSheetIntent)  {
    when (intent) {
        is NoServiceSheetIntent.SetSelectedLocation -> {
            intent.location.point?.let { point ->
                mapsViewModel.onIntent(SetLocations(listOf(point)))
                mapsViewModel.onIntent(AnimateToFirstLocation)
            }
        }

        is NoServiceSheetIntent.SetServiceState -> {
            intent { reduce { state.copy(serviceAvailable = intent.available) } }
        }
    }
}

fun HomeViewModel.onIntent(intent: MainSheetIntent) = intent {
    when (intent) {
        is MainSheetIntent.OrderTaxiSheetIntent.SetSelectedLocation -> {
            if (state.destinations.isEmpty()) {
                intent.location.point?.let { point ->
                    mapsViewModel.onIntent(SetLocations(listOf(point)))
                    mapsViewModel.onIntent(AnimateToFirstLocation)
                }
            } else {
                reduce { state.copy(location = intent.location) }
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
            reduce {
                state.copy(
                    carArrivalInMinutes = intent.timeout,
                    drivers = if (state.order == null) intent.drivers else emptyList()
                )
            }
        }

        is MainSheetIntent.OrderTaxiSheetIntent.SetServiceState -> {
            reduce { state.copy(serviceAvailable = intent.available) }
        }

        MainSheetIntent.PaymentMethodSheetIntent.OnAddNewCard -> {
            postSideEffect(NavigateToAddCard)
        }

        MainSheetIntent.FooterIntent.Register -> {
            postSideEffect(NavigateToRegister)
        }

        else -> {
            // Other intents are handled on main sheet
        }
    }
}

fun HomeViewModel.onIntent(intent: SearchCarSheetIntent) = intent {
    when (intent) {
        SearchCarSheetIntent.AddNewOrder -> clearState()
        SearchCarSheetIntent.ZoomOut -> mapsViewModel.onIntent(ZoomOut)
        is SearchCarSheetIntent.OnCancelled -> {
            val orderId = intent.orderId ?: return@intent
            postSideEffect(NavigateToCancelled(orderId))
        }

        else -> {
            /* no-op */
        }
    }
}

fun HomeViewModel.onIntent(intent: ClientWaitingSheetIntent) = intent {
    when (intent) {
        ClientWaitingSheetIntent.AddNewOrder -> {
            clearState()
        }

        is ClientWaitingSheetIntent.OnCancelled -> {
            state.orderId?.let { orderId ->
                postSideEffect(NavigateToCancelled(orderId))
            }
        }

        is ClientWaitingSheetIntent.UpdateRoute -> {
            /* no-op */
        }

        else -> {
            /* no-op */
        }
    }
}

fun HomeViewModel.onIntent(intent: DriverWaitingSheetIntent) = intent {
    when (intent) {
        DriverWaitingSheetIntent.AddNewOrder -> {
            clearState()
        }

        is DriverWaitingSheetIntent.OnCancelled -> {
            state.orderId?.let { orderId ->
                postSideEffect(NavigateToCancelled(orderId))
            }
        }

        else -> {
            /* no-op */
        }
    }
}

fun HomeViewModel.onIntent(intent: OnTheRideSheetIntent) {
    when (intent) {
        OnTheRideSheetIntent.AddNewOrder -> clearState()
        else -> {
            /* no-op */
        }
    }
}

fun HomeViewModel.onIntent(intent: OrderCanceledSheetIntent) {
    when (intent) {
        OrderCanceledSheetIntent.StartNewOrder -> clearState()
    }
}

fun HomeViewModel.onIntent(intent: FeedbackSheetIntent) {
    when (intent) {
        FeedbackSheetIntent.OnCompleteOrder -> clearState()
        else -> {
            /* no-op */
        }
    }
}

fun HomeViewModel.onIntent(intent: CancelReasonIntent) {
    when (intent) {
        CancelReasonIntent.NavigateBack -> {
            setSheet(null)
            clearState()
        }
    }
}