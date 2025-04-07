package uz.yalla.client.feature.map.presentation.view

import android.Manifest
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.map.ConcreteGoogleMap
import uz.yalla.client.core.common.map.MapStrategy
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.state.rememberPermissionState
import uz.yalla.client.core.data.enums.MapType
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.map.presentation.model.MapUIState
import uz.yalla.client.feature.map.presentation.model.MapViewModel
import uz.yalla.client.feature.map.presentation.view.drawer.MapDrawer
import uz.yalla.client.feature.map.presentation.view.drawer.MapDrawerIntent
import uz.yalla.client.feature.order.domain.model.response.order.toCommonExecutor
import uz.yalla.client.feature.order.presentation.cancel_reason.ORDER_ID
import uz.yalla.client.feature.order.presentation.client_waiting.CLIENT_WAITING_ROUTE
import uz.yalla.client.feature.order.presentation.client_waiting.navigateToClientWaitingSheet
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingIntent
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingSheet
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.driver_waiting.DRIVER_WAITING_ROUTE
import uz.yalla.client.feature.order.presentation.driver_waiting.navigateToDriverWaitingSheet
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingIntent
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingSheet
import uz.yalla.client.feature.order.presentation.feedback.FEEDBACK_ROUTE
import uz.yalla.client.feature.order.presentation.feedback.navigateToFeedbackSheet
import uz.yalla.client.feature.order.presentation.feedback.view.FeedbackSheet
import uz.yalla.client.feature.order.presentation.feedback.view.FeedbackSheetIntent
import uz.yalla.client.feature.order.presentation.main.MAIN_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.main.navigateToMainSheet
import uz.yalla.client.feature.order.presentation.main.view.MainSheet
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.OrderTaxiSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.PaymentMethodSheetIntent
import uz.yalla.client.feature.order.presentation.no_service.NO_SERVICE_ROUTE
import uz.yalla.client.feature.order.presentation.no_service.navigateToNoServiceSheet
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceIntent
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceSheet
import uz.yalla.client.feature.order.presentation.on_the_ride.ON_THE_RIDE_ROUTE
import uz.yalla.client.feature.order.presentation.on_the_ride.navigateToOnTheRideSheet
import uz.yalla.client.feature.order.presentation.on_the_ride.view.OnTheRideSheet
import uz.yalla.client.feature.order.presentation.on_the_ride.view.OnTheRideSheetIntent
import uz.yalla.client.feature.order.presentation.order_canceled.ORDER_CANCELED_ROUTE
import uz.yalla.client.feature.order.presentation.order_canceled.navigateToCanceledOrder
import uz.yalla.client.feature.order.presentation.order_canceled.view.OrderCanceledSheet
import uz.yalla.client.feature.order.presentation.order_canceled.view.OrderCanceledSheetIntent
import uz.yalla.client.feature.order.presentation.search.SEARCH_CAR_ROUTE
import uz.yalla.client.feature.order.presentation.search.navigateToSearchForCarBottomSheet
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheet
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetIntent

@Composable
fun MapRoute(
    onPermissionDenied: () -> Unit,
    onProfileClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onPaymentTypeClick: () -> Unit,
    onAddressesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCancel: (Int) -> Unit,
    onAddNewCard: () -> Unit,
    onAboutAppClick: () -> Unit,
    onContactUsClick: () -> Unit,
    onBecomeDriverClick: (String, String) -> Unit,
    onInviteFriendClick: (String, String) -> Unit,
    vm: MapViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val permissionsGranted by rememberPermissionState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val state by vm.uiState.collectAsState()
    val isMapEnabled by vm.isMapEnabled.collectAsState()
    val hamburgerButtonState by vm.hamburgerButtonState.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val map: MapStrategy by remember {
        mutableStateOf(
            when (AppPreferences.mapType) {
                MapType.Google -> ConcreteGoogleMap()
                MapType.Gis -> ConcreteGoogleMap()
            }
        )
    }
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: MAIN_SHEET_ROUTE

    BackHandler {
        when {
            drawerState.isOpen -> scope.launch { drawerState.close() }

            state.destinations.isNotEmpty() -> {
                val destinations = state.destinations.toMutableList()
                destinations.removeAt(destinations.lastIndex)
                vm.updateState(state.copy(destinations = destinations))
            }

            state.selectedOrder == null -> (context as? Activity)?.finish()
        }
    }

    LaunchedEffect(permissionsGranted) {
        if (!permissionsGranted) onPermissionDenied()
    }

    LaunchedEffect(Unit) {
        launch(Dispatchers.Main) {
            map.isMarkerMoving.collectLatest { isMarkerMoving ->
                if (state.destinations.isEmpty()) {
                    when {
                        isMarkerMoving.not() && state.selectedOrder == null -> {
                            withContext(Dispatchers.IO) {
                                vm.getAddressName(map.mapPoint.value)
                            }
                        }

                        state.route.isEmpty() && state.selectedOrder?.status == null -> {
                            vm.setStateToNotFound()
                        }
                    }
                }
            }
        }

        launch(Dispatchers.Main.immediate) {
            SheetCoordinator.currentSheetState.collectLatest { sheetState ->
                sheetState?.let { (_, height) ->
                    handleSheetHeightChange(height, map, vm, state)
                }
            }
        }

        launch(Dispatchers.IO) {
            vm.getMe()
        }
    }

    fun shouldNavigateToSheet(routePattern: String, orderId: Int): Boolean {
        val currentDestination = navController.currentDestination
        val thisRoute = currentDestination?.route ?: ""

        if (!thisRoute.contains(routePattern)) {
            return true
        }

        val currentBackStackEntry = navController.currentBackStackEntry
        val currentOrderId = currentBackStackEntry?.arguments?.getInt(ORDER_ID, -1) ?: -1

        return currentOrderId != orderId
    }

    LaunchedEffect(state.selectedOrder) {
        val order = state.selectedOrder

        if (order?.status !in OrderStatus.nonInteractive) order?.let {
            vm.updateState(
                state.copy(
                    drivers = listOf(order.executor.toCommonExecutor())
                )
            )
        }

        when (order?.status) {
            OrderStatus.Appointed -> {
                if (shouldNavigateToSheet(CLIENT_WAITING_ROUTE, order.id)) {
                    navController.navigateToClientWaitingSheet(orderId = order.id)
                }

                order.executor.coords.let { coordinate ->
                    map.move(to = MapPoint(coordinate.lat, coordinate.lng))
                }
            }

            OrderStatus.AtAddress -> {
                if (shouldNavigateToSheet(DRIVER_WAITING_ROUTE, order.id)) {
                    navController.navigateToDriverWaitingSheet(orderId = order.id)
                }

                order.executor.coords.let { coordinate ->
                    map.move(to = MapPoint(coordinate.lat, coordinate.lng))
                }
            }

            OrderStatus.Canceled -> {
                val currentDestination = navController.currentDestination?.route ?: ""
                if (!currentDestination.contains(ORDER_CANCELED_ROUTE)) {
                    vm.clearState()
                    navController.navigateToCanceledOrder()
                }
            }

            OrderStatus.Completed -> {
                if (shouldNavigateToSheet(FEEDBACK_ROUTE, order.id)) {
                    navController.navigateToFeedbackSheet(orderId = order.id)
                }
            }

            OrderStatus.InFetters -> {
                if (shouldNavigateToSheet(ON_THE_RIDE_ROUTE, order.id)) {
                    navController.navigateToOnTheRideSheet(orderId = order.id)
                }

                order.executor.coords.let { coordinate ->
                    map.move(to = MapPoint(coordinate.lat, coordinate.lng))
                }
            }

            null -> {
                map.updateLocations(emptyList())
                val currentDestination = navController.currentDestination?.route ?: ""
                val isInOrderFlow = listOf(
                    CLIENT_WAITING_ROUTE, DRIVER_WAITING_ROUTE, ON_THE_RIDE_ROUTE,
                    FEEDBACK_ROUTE, SEARCH_CAR_ROUTE, ORDER_CANCELED_ROUTE
                ).any { currentDestination.contains(it) }

                if (!currentDestination.contains(MAIN_SHEET_ROUTE) &&
                    !currentDestination.contains(NO_SERVICE_ROUTE) &&
                    !isInOrderFlow
                ) {
                    navController.navigateToMainSheet()
                }
            }

            else -> {
                if (shouldNavigateToSheet(SEARCH_CAR_ROUTE, order.id)) {
                    order.taxi.routes.firstOrNull()?.coords?.let { coordinate ->
                        navController.navigateToSearchForCarBottomSheet(
                            tariffId = order.taxi.tariffId,
                            orderId = order.id,
                            point = MapPoint(
                                lat = coordinate.lat,
                                lng = coordinate.lng
                            )
                        )
                    }
                }

                order.taxi.routes.firstOrNull()?.coords.let { coordinate ->
                    if (coordinate?.lat != null)
                        map.move(to = MapPoint(coordinate.lat, coordinate.lng))
                }
            }
        }
    }

    LaunchedEffect(currentRoute) {
        when {
            currentRoute == MAIN_SHEET_ROUTE || currentRoute == NO_SERVICE_ROUTE -> {
                MainSheet.intentFlow.collect { intent ->
                    when (intent) {
                        is OrderTaxiSheetIntent.SetSelectedLocation -> {
                            vm.updateState(state.copy(selectedLocation = intent.selectedLocation))

                            if (state.route.isEmpty()) {
                                intent.selectedLocation.point?.let {
                                    scope.launch(Dispatchers.Main) {
                                        map.animate(to = it)
                                    }
                                }
                            }
                        }

                        is OrderTaxiSheetIntent.SetDestinations -> {
                            vm.updateState(state.copy(destinations = intent.destinations))
                        }

                        is OrderTaxiSheetIntent.AddDestination -> {
                            vm.updateState(state.copy(destinations = state.destinations + intent.destination))
                        }

                        is OrderTaxiSheetIntent.OrderCreated -> {
                            vm.setSelectedOrder(intent.order)
                            vm.updateState(state.copy(markerState = YallaMarkerState.Searching))
                        }

                        is OrderTaxiSheetIntent.SetTimeout -> {
                            vm.updateState(
                                state.copy(
                                    timeout = intent.timeout,
                                    drivers = intent.drivers
                                )
                            )
                        }

                        is OrderTaxiSheetIntent.SetServiceState -> {
                            vm.updateState(state.copy(hasServiceProvided = intent.available))
                        }

                        is PaymentMethodSheetIntent.OnAddNewCard -> {
                            onAddNewCard()
                        }

                        else -> {}
                    }
                }
            }

            currentRoute.contains(SEARCH_CAR_ROUTE) -> {
                SearchCarSheet.intentFlow.collectLatest { intent ->
                    when (intent) {
                        is SearchCarSheetIntent.OnCancelled -> {
                            navController.navigateToMainSheet()
                            intent.orderId?.let { onCancel(state.selectedOrder?.id ?: it) }
                            vm.clearState()

                        }

                        is SearchCarSheetIntent.AddNewOrder -> {
                            vm.clearState()
                            navController.navigateToMainSheet()
                        }

                        is SearchCarSheetIntent.ZoomOut -> {
                            map.zoomOut()
                        }

                        else -> {}
                    }
                }
            }

            currentRoute.contains(CLIENT_WAITING_ROUTE) -> {
                ClientWaitingSheet.intentFlow.collectLatest { intent ->
                    when (intent) {
                        is ClientWaitingIntent.AddNewOrder -> {
                            vm.clearState()
                            navController.navigateToMainSheet()
                        }

                        is ClientWaitingIntent.OnCancelled -> {
                            vm.clearState()
                            intent.orderId?.let { onCancel(it) }
                        }

                        is ClientWaitingIntent.UpdateRoute -> {
                            vm.updateState(state.copy(driverRoute = intent.route))
                        }
                    }
                }
            }

            currentRoute.contains(DRIVER_WAITING_ROUTE) -> {
                DriverWaitingSheet.intentFlow.collectLatest { intent ->
                    when (intent) {
                        is DriverWaitingIntent.OnCancelled -> {
                            navController.navigateToMainSheet()
                            intent.orderId?.let { onCancel(state.selectedOrder?.id ?: it) }
                            vm.clearState()
                        }

                        is DriverWaitingIntent.AddNewOrder -> {
                            vm.clearState()
                            navController.navigateToMainSheet()
                        }
                    }
                }
            }

            currentRoute.contains(ON_THE_RIDE_ROUTE) -> {
                OnTheRideSheet.intentFlow.collectLatest { intent ->
                    when (intent) {
                        is OnTheRideSheetIntent.AddNewOrder -> {
                            vm.clearState()
                            navController.navigateToMainSheet()
                        }
                    }
                }
            }

            currentRoute.contains(ORDER_CANCELED_ROUTE) -> {
                OrderCanceledSheet.intentFlow.collectLatest { intent ->
                    when (intent) {
                        is OrderCanceledSheetIntent.StartNewOrder -> {
                            navController.navigateToMainSheet()
                        }
                    }
                }
            }

            currentRoute.contains(FEEDBACK_ROUTE) -> {
                FeedbackSheet.intentFlow.collectLatest { intent ->
                    when (intent) {
                        is FeedbackSheetIntent.OnCompleteOrder -> {
                            vm.clearState()
                            navController.navigateToMainSheet()
                        }
                    }
                }
            }

            currentRoute.contains(NO_SERVICE_ROUTE) -> {
                NoServiceSheet.intentFlow.collectLatest { intent ->
                    when (intent) {
                        is NoServiceIntent.SetSelectedLocation -> {
                            intent.location.point?.let { map.animate(to = it) }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(state.hasServiceProvided, state.selectedLocation) {
        if (state.hasServiceProvided == true) {
            val currentDestination = navController.currentDestination?.route ?: ""
            if (!currentDestination.contains(MAIN_SHEET_ROUTE)) {
                val orderRelatedRoutes = listOf(
                    CLIENT_WAITING_ROUTE, DRIVER_WAITING_ROUTE, ON_THE_RIDE_ROUTE,
                    FEEDBACK_ROUTE, SEARCH_CAR_ROUTE, ORDER_CANCELED_ROUTE
                )
                val isInOrderFlow = orderRelatedRoutes.any { currentDestination.contains(it) }

                if (!isInOrderFlow) {
                    navController.navigateToMainSheet()
                }
            }
        } else if (state.hasServiceProvided == false) {
            val currentDestination = navController.currentDestination?.route ?: ""
            if (!currentDestination.contains(NO_SERVICE_ROUTE)) {
                navController.navigateToNoServiceSheet()
            }
        }
    }

    LaunchedEffect(currentRoute, state.route, state.driverRoute) {
        if (currentRoute == CLIENT_WAITING_ROUTE) {
            map.updateRoute(state.driverRoute)
            if (state.driverRoute.isEmpty()) {
                state.selectedLocation?.point?.let { map.animate(it) }
            } else if (state.selectedOrder?.status !in OrderStatus.nonInteractive) {
                map.animateToFitBounds(state.driverRoute)
            }
        } else {
            map.updateRoute(state.route)
            if (state.route.isEmpty()) {
                state.selectedLocation?.point?.let { map.animate(it) }
            } else if (state.selectedOrder?.status !in OrderStatus.nonInteractive) {
                map.animateToFitBounds(state.route)
            }
        }
    }

    LaunchedEffect(state.selectedLocation, state.destinations) {
        val start = state.selectedLocation?.point?.let {
            MapPoint(it.lat, it.lng)
        }
        val dest = state.destinations.mapNotNull {
            it.point?.let { point -> MapPoint(point.lat, point.lng) }
        }
        val locations = listOfNotNull(start) + dest

        map.updateLocations(locations)
    }

    LaunchedEffect(state.drivers) {
        map.updateDrivers(state.drivers)
    }

    MapDrawer(
        user = state.user,
        drawerState = drawerState,
        onIntent = { intent ->
            when (intent) {
                is MapDrawerIntent.Profile -> onProfileClick()
                is MapDrawerIntent.OrdersHistory -> onOrderHistoryClick()
                is MapDrawerIntent.PaymentType -> onPaymentTypeClick()
                is MapDrawerIntent.MyPlaces -> onAddressesClick()
                is MapDrawerIntent.Settings -> onSettingsClick()
                is MapDrawerIntent.AboutTheApp -> onAboutAppClick()
                is MapDrawerIntent.ContactUs -> onContactUsClick()
                is MapDrawerIntent.BecomeADriver -> onBecomeDriverClick(intent.title, intent.url)
                is MapDrawerIntent.InviteFriend -> onInviteFriendClick(intent.title, intent.url)
            }
        },
        content = {
            MapScreen(
                map = map,
                isMapEnabled = isMapEnabled,
                state = state,
                moveCameraButtonState = state.moveCameraButtonState,
                hamburgerButtonState = hamburgerButtonState,
                navController = navController
            ) { intent ->
                when (intent) {
                    is MapScreenIntent.MapOverlayIntent.ClickShowOrders -> {
                        vm.updateState(state.copy(isActiveOrdersSheetVisibility = true))
                    }

                    is MapScreenIntent.OnDismissActiveOrders -> {
                        vm.updateState(state.copy(isActiveOrdersSheetVisibility = false))
                    }

                    is MapScreenIntent.MapOverlayIntent.MoveToFirstLocation -> {
                        state.selectedLocation?.point?.let {
                            map.animate(it)
                        }
                    }

                    is MapScreenIntent.MapOverlayIntent.AnimateToMyLocation -> {
                        map.animateToMyLocation()
                    }

                    is MapScreenIntent.MapOverlayIntent.MoveToMyRoute -> {
                        map.animateToFitBounds(state.route)
                    }

                    is MapScreenIntent.MapOverlayIntent.NavigateBack -> {
                        vm.clearState()
                        navController.navigateToMainSheet()
                    }

                    is MapScreenIntent.MapOverlayIntent.OpenDrawer -> {
                        scope.launch {
                            drawerState.open()
                        }
                    }

                    is MapScreenIntent.SetShowingOrder -> {
                        vm.setSelectedOrder(intent.order)
                    }

                    is MapScreenIntent.MapOverlayIntent.MoveToMyLocation -> {
                        map.moveToMyLocation()
                    }
                }
            }
        }
    )

    if (state.loading) {
        LoadingDialog()
    }
}

private suspend fun handleSheetHeightChange(
    height: Dp,
    map: MapStrategy,
    vm: MapViewModel,
    state: MapUIState
) {
    vm.updateState(state.copy(sheetHeight = height))
    awaitFrame()

    state.selectedOrder?.taxi?.routes?.firstOrNull()?.coords?.let { coordinate ->
        map.move(to = MapPoint(coordinate.lat, coordinate.lng))
    } ?: run {
        state.selectedLocation?.point?.let { map.move(to = it) }
            ?: map.move(to = map.mapPoint.value)
    }
}