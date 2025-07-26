package uz.yalla.client.feature.map.presentation.new_version.model

import uz.yalla.client.core.common.maps.MapsIntent
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.feature.map.presentation.new_version.intent.MapEffect
import uz.yalla.client.feature.map.presentation.new_version.intent.MapIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent

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
            reduce {
                state.copy(
                    ordersSheetVisible = true
                )
            }
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
            // Handle navigation back
            // This would typically involve navigating back in the app's navigation stack
            // or closing a specific screen or dialog
        }

        MapIntent.MapOverlayIntent.OnClickBonus -> {
            MainSheetChannel.setBonusVisibility(true)
        }

        MapIntent.MapOverlayIntent.OpenDrawer -> {
            // This is handled in MapScreen.kt
        }

        MapIntent.OnDismissActiveOrders -> reduce {
            state.copy(
                ordersSheetVisible = false
            )
        }

        is MapIntent.SetShowingOrder -> reduce {
            state.copy(
                order = intent.order,
                orderId = intent.order.id,
                ordersSheetVisible = false
            )
        }
    }
}

fun MViewModel.onIntent(intent: MainSheetIntent) = intent {
    when (intent) {
        is MainSheetIntent.OrderTaxiSheetIntent.SetSelectedLocation -> {
            if (state.orderId == null && state.route.isEmpty())
                intent.location.point?.let { point ->
                    mapsViewModel.onIntent(MapsIntent.AnimateTo(point))
                }
            else reduce {
                state.copy(location = intent.location)
            }
        }

        is MainSheetIntent.OrderTaxiSheetIntent.SetDestinations -> {
            reduce { state.copy(destinations = intent.destinations) }

            if (intent.destinations.isEmpty()) {
                state.location?.point?.let { point ->
                    mapsViewModel.onIntent(MapsIntent.AnimateTo(point))
                }
            } else {
                getRouting()
            }
        }

        is MainSheetIntent.OrderTaxiSheetIntent.AddDestination -> {
            reduce { state.copy(destinations = state.destinations + intent.destination) }
        }

        is MainSheetIntent.OrderTaxiSheetIntent.OrderCreated -> {
            reduce {
                state.copy(
                    order = intent.order,
                    orderId = intent.order.id,
                    markerState = YallaMarkerState.Searching
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

        }

        MainSheetIntent.FooterIntent.Register -> {

        }

        else -> {
            // Other intents are handled on main sheet
        }
    }
}