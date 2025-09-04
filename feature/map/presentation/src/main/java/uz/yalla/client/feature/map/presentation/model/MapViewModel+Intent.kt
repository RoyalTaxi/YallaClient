package uz.yalla.client.feature.map.presentation.model

import uz.yalla.client.core.common.maps.core.model.MapsIntent
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.feature.map.presentation.intent.MapEffect
import uz.yalla.client.feature.map.presentation.intent.MapIntent
import uz.yalla.client.feature.order.domain.model.response.order.toCommonExecutor
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

fun MViewModel.onIntent(intent: MapIntent) {
    when (intent) {
        is MapIntent.MapOverlayIntent.MoveToMyLocation -> {
            mapsViewModel.onIntent(MapsIntent.MoveToMyLocation(intent.context))
        }

        is MapIntent.MapOverlayIntent.AnimateToMyLocation -> {
            mapsViewModel.onIntent(MapsIntent.AnimateToMyLocation(intent.context))
        }

        MapIntent.MapOverlayIntent.AskForEnable -> {
            launchEffect(MapEffect.EnableLocation)
        }

        MapIntent.MapOverlayIntent.AskForPermission -> {
            launchEffect(MapEffect.GrantLocation)
        }

        MapIntent.MapOverlayIntent.ClickShowOrders -> {
            updateState { it.copy(ordersSheetVisible = true) }
        }

        MapIntent.MapOverlayIntent.MoveToFirstLocation -> {
            stateFlow.value.location?.point?.let { point ->
                mapsViewModel.onIntent(MapsIntent.AnimateTo(point))
            }
        }

        MapIntent.MapOverlayIntent.MoveToMyRoute -> {
            mapsViewModel.onIntent(MapsIntent.AnimateFitBounds)
        }

        MapIntent.MapOverlayIntent.NavigateBack -> {
            if (stateFlow.value.order == null) removeLastDestination()
            else clearState()
        }

        MapIntent.MapOverlayIntent.OnClickBonus -> {
            MainSheetChannel.setBonusVisibility(true)
        }

        MapIntent.MapOverlayIntent.OpenDrawer -> {
            // This is handled in MapScreen.kt
        }

        MapIntent.OnDismissActiveOrders -> {
            updateState { it.copy(ordersSheetVisible = false) }
        }

        is MapIntent.SetShowingOrder -> {
            mapsViewModel.onIntent(MapsIntent.UpdateDriver(intent.order.executor.toCommonExecutor()))
            // Clear any previously drawn route to avoid flashing planned route
            mapsViewModel.onIntent(MapsIntent.UpdateRoute(emptyList()))

            // Extract pickup + destinations from selected order to ensure markers
            val first = intent.order.taxi.routes.firstOrNull()
            val others = intent.order.taxi.routes.drop(1)

            updateState {
                it.copy(
                    order = intent.order,
                    orderId = intent.order.id,
                    ordersSheetVisible = false
                )
            }
        }

        is MapIntent.MapOverlayIntent.RefocusLastState -> {
            // Avoid extra animations on layout changes; just refresh order state
            if (stateFlow.value.serviceAvailable == false) return
            getActiveOrder()
        }

        is MapIntent.SetShowingOrderId -> {
            updateState { it.copy(orderId = intent.orderId) }
        }

        is MapIntent.SetPollingSuppressed -> {
            updateState { it.copy(suppressOrderPolling = intent.suppressed) }
        }
    }
}

fun MViewModel.onIntent(intent: NoServiceSheetIntent) {
    when (intent) {
        is NoServiceSheetIntent.SetSelectedLocation -> {
            intent.location.point?.let { point ->
                mapsViewModel.onIntent(MapsIntent.AnimateTo(point))
            }
        }
    }
}

fun MViewModel.onIntent(intent: MainSheetIntent) {
    when (intent) {
        is MainSheetIntent.OrderTaxiSheetIntent.SetSelectedLocation -> {
            if (stateFlow.value.destinations.isEmpty())
                intent.location.point?.let { point ->
                    mapsViewModel.onIntent(MapsIntent.AnimateTo(point))
                }
            else
                updateState { it.copy(location = intent.location) }
        }

        is MainSheetIntent.OrderTaxiSheetIntent.SetDestinations -> {
            updateState { it.copy(destinations = intent.destinations) }
        }

        is MainSheetIntent.OrderTaxiSheetIntent.AddDestination -> {
            updateState { it.copy(destinations = it.destinations + intent.destination) }
        }

        is MainSheetIntent.OrderTaxiSheetIntent.OrderCreated -> {
            updateState {
                it.copy(
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
            updateState {
                it.copy(
                    carArrivalInMinutes = intent.timeout,
                    drivers = if (it.order == null) intent.drivers else emptyList()
                )
            }
        }

        is MainSheetIntent.OrderTaxiSheetIntent.SetServiceState -> {
            updateState { it.copy(serviceAvailable = intent.available) }
        }

        MainSheetIntent.PaymentMethodSheetIntent.OnAddNewCard -> {
            launchEffect(MapEffect.NavigateToAddCard)
        }

        MainSheetIntent.FooterIntent.Register -> {
            launchEffect(MapEffect.NavigateToRegister)
        }

        else -> {
            // Other intents are handled on main sheet
        }
    }
}

fun MViewModel.onIntent(intent: SearchCarSheetIntent) {
    when (intent) {
        SearchCarSheetIntent.AddNewOrder -> clearState()
        SearchCarSheetIntent.ZoomOut -> mapsViewModel.onIntent(MapsIntent.ZoomOut)
        is SearchCarSheetIntent.OnCancelled -> {
            val orderId = intent.orderId ?: return
            launchEffect(MapEffect.NavigateToCancelled(orderId))
        }

        else -> {
            /* no-op */
        }
    }
}

fun MViewModel.onIntent(intent: ClientWaitingSheetIntent) {
    when (intent) {
        ClientWaitingSheetIntent.AddNewOrder -> clearState()
        is ClientWaitingSheetIntent.OnCancelled -> {
            stateFlow.value.orderId?.let { orderId ->
                launchEffect(MapEffect.NavigateToCancelled(orderId))
            }
        }

        is ClientWaitingSheetIntent.UpdateRoute -> {
            // Draw live route from driver -> client while waiting.
            // Backend routing for this is triggered from ClientWaitingViewModel
            // and delivered via this intent.
            val orderStatus = stateFlow.value.order?.status

            // When driver is not yet at address, render the driver->client route.
            if (orderStatus != uz.yalla.client.core.domain.model.OrderStatus.AtAddress) {
                mapsViewModel.onIntent(
                    MapsIntent.UpdateRoute(intent.route)
                )
                // Fit bounds to the live driver route for better context
                mapsViewModel.onIntent(MapsIntent.AnimateFitBounds)
            } else {
                // Once driver reached the pickup, prefer showing planned route
                mapsViewModel.onIntent(MapsIntent.UpdateRoute(stateFlow.value.route))
                mapsViewModel.onIntent(MapsIntent.AnimateFitBounds)
            }
        }

        else -> {
            /* no-op */
        }
    }
}

fun MViewModel.onIntent(intent: DriverWaitingSheetIntent) {
    when (intent) {
        DriverWaitingSheetIntent.AddNewOrder -> clearState()
        is DriverWaitingSheetIntent.OnCancelled -> {
            stateFlow.value.orderId?.let { orderId ->
                launchEffect(MapEffect.NavigateToCancelled(orderId))
            }
        }

        else -> {
            /* no-op */
        }
    }
}

fun MViewModel.onIntent(intent: OnTheRideSheetIntent) {
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

fun MViewModel.onIntent(intent: CancelReasonIntent) {
    when (intent) {
        CancelReasonIntent.NavigateBack -> clearState()
        else -> {
            /* no-op */
        }
    }
}
