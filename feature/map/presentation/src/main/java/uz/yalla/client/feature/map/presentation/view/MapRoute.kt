package uz.yalla.client.feature.map.presentation.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.map.MapStrategy
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.map.presentation.LocationServiceReceiver
import uz.yalla.client.feature.map.presentation.R
import uz.yalla.client.feature.map.presentation.model.MapUIState
import uz.yalla.client.feature.map.presentation.model.MapViewModel
import uz.yalla.client.feature.map.presentation.view.drawer.MapDrawer
import uz.yalla.client.feature.map.presentation.view.drawer.MapDrawerIntent
import uz.yalla.client.feature.order.domain.model.response.order.toCommonExecutor
import uz.yalla.client.feature.order.presentation.cancel_reason.ORDER_ID
import uz.yalla.client.feature.order.presentation.client_waiting.CLIENT_WAITING_ROUTE
import uz.yalla.client.feature.order.presentation.client_waiting.navigateToClientWaitingSheet
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingSheetChannel
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingSheetIntent
import uz.yalla.client.feature.order.presentation.coordinator.SheetCoordinator
import uz.yalla.client.feature.order.presentation.driver_waiting.DRIVER_WAITING_ROUTE
import uz.yalla.client.feature.order.presentation.driver_waiting.navigateToDriverWaitingSheet
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingSheetChannel
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingSheetIntent
import uz.yalla.client.feature.order.presentation.feedback.FEEDBACK_ROUTE
import uz.yalla.client.feature.order.presentation.feedback.navigateToFeedbackSheet
import uz.yalla.client.feature.order.presentation.main.MAIN_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.main.navigateToMainSheet
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.OrderTaxiSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.PaymentMethodSheetIntent
import uz.yalla.client.feature.order.presentation.no_service.NO_SERVICE_ROUTE
import uz.yalla.client.feature.order.presentation.no_service.navigateToNoServiceSheet
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceSheetChannel
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceSheetIntent
import uz.yalla.client.feature.order.presentation.on_the_ride.ON_THE_RIDE_ROUTE
import uz.yalla.client.feature.order.presentation.on_the_ride.navigateToOnTheRideSheet
import uz.yalla.client.feature.order.presentation.on_the_ride.view.OnTheRideSheetChannel
import uz.yalla.client.feature.order.presentation.on_the_ride.view.OnTheRideSheetIntent
import uz.yalla.client.feature.order.presentation.order_canceled.ORDER_CANCELED_ROUTE
import uz.yalla.client.feature.order.presentation.order_canceled.navigateToCanceledOrder
import uz.yalla.client.feature.order.presentation.order_canceled.view.OrderCanceledSheetChannel
import uz.yalla.client.feature.order.presentation.order_canceled.view.OrderCanceledSheetIntent
import uz.yalla.client.feature.order.presentation.search.SEARCH_CAR_ROUTE
import uz.yalla.client.feature.order.presentation.search.navigateToSearchForCarBottomSheet
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetChannel
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetIntent
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private val LocalDrawerState = compositionLocalOf { DrawerState(DrawerValue.Closed) }

@OptIn(FlowPreview::class)
@Composable
fun MapRoute(
    onRegister: () -> Unit,
    onProfileClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onPaymentTypeClick: () -> Unit,
    onAddressesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCancel: (Int) -> Unit,
    onAddNewCard: () -> Unit,
    onAboutAppClick: () -> Unit,
    onContactUsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onClickBonuses: () -> Unit,
    onBecomeDriverClick: (String, String) -> Unit,
    onInviteFriendClick: (String, String) -> Unit,
    vm: MapViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val prefs = koinInject<AppPreferences>()
    var permissionsGranted by remember { mutableStateOf(true) }
    var isLocationEnabled by remember { mutableStateOf(true) }

    var showPermissionDialog by remember { mutableStateOf(false) }

    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        showPermissionDialog = !granted
    }

    val locationServiceReceiver = rememberUpdatedState(
        LocationServiceReceiver { isEnabled ->
            isLocationEnabled = isEnabled
        }
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkLocation(
                    context = context,
                    provideLocationState = { state ->
                        isLocationEnabled = state
                    },
                    providePermissionState = { state ->
                        permissionsGranted = state
                    },
                    provideShowPermissionDialog = { state ->
                        if (showPermissionDialog)
                            showPermissionDialog = state
                    }
                )
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        val receiver = locationServiceReceiver.value
        val filter =
            android.content.IntentFilter(android.location.LocationManager.PROVIDERS_CHANGED_ACTION)

        context.registerReceiver(receiver, filter)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            context.unregisterReceiver(receiver)
        }
    }

    val state by vm.uiState.collectAsStateWithLifecycle()
    val hamburgerButtonState by vm.hamburgerButtonState.collectAsStateWithLifecycle()
    var drawerState = rememberDrawerState(DrawerValue.Closed)

    val mapType by prefs.mapType.collectAsState(initial = null)

    // ✅ FIX: Get the map instance from the ViewModel
    val map by vm.map.collectAsStateWithLifecycle()

    // ✅ FIX: Trigger map initialization from a LaunchedEffect
    // This runs only when mapType changes from null to a real value
    LaunchedEffect(mapType) {
        mapType?.let {
            vm.initializeMap(it)
        }
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    CompositionLocalProvider(
        LocalDrawerState provides rememberDrawerState(DrawerValue.Closed)
    ) {
        drawerState = LocalDrawerState.current
    }

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

    // ✅ FIX 1: Move heavy operations to background dispatcher
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (isActive) {
                // Move to IO dispatcher to prevent main thread blocking
                withContext(Dispatchers.IO) {
                    vm.getActiveOrders()
                }
                delay(5.seconds)
            }
        }
    }

    map?.let { mapInstance ->
        LaunchedEffect(true) {
            launch(Dispatchers.Default) {
                vm.getMe()
                vm.getNotificationsCount()
                vm.getSettingConfig()
            }

            launch {
                vm.collectMarkerMovement(
                    point = mapInstance.mapPoint,
                    collectable = mapInstance.isMarkerMoving
                )
            }

            launch {
                SheetCoordinator.currentSheetState.collectLatest { sheetState ->
                    sheetState?.let { (_, height) ->
                        optimizedHandleSheetHeightChange(height, mapInstance, vm, state)
                    }
                }
            }
        }

        LaunchedEffect(state.timeout, state.orderEndsInMinutes) {
            mapInstance.updateCarArrivesInMinutes(state.timeout)
            mapInstance.updateOrderEndsInMinutes(state.orderEndsInMinutes)
        }

        // ✅ FIX 2: Debounce map operations to prevent excessive updates
        LaunchedEffect(state.selectedOrder) {
            // Debounce rapid state changes
            delay(50)

            val order = state.selectedOrder

            mapInstance.updateOrderStatus(order?.status)

            if (order?.status !in OrderStatus.nonInteractive) order?.let {
                vm.updateState(state.copy(drivers = listOf(order.executor.toCommonExecutor())))
            }

            when (order?.status) {
                OrderStatus.Appointed -> {
                    if (navController.shouldNavigateToSheet(CLIENT_WAITING_ROUTE, order.id)) {
                        // Defer navigation to next frame to prevent blocking
                        scope.launch(Dispatchers.Main.immediate) {
                            navController.navigateToClientWaitingSheet(orderId = order.id)
                        }
                    }

                    order.executor.coords.let { coordinate ->
                        mapInstance.move(to = MapPoint(coordinate.lat, coordinate.lng))
                    }
                }

                OrderStatus.AtAddress -> {
                    if (navController.shouldNavigateToSheet(DRIVER_WAITING_ROUTE, order.id)) {
                        scope.launch(Dispatchers.Main.immediate) {
                            navController.navigateToDriverWaitingSheet(orderId = order.id)
                        }
                    }

                    order.executor.coords.let { coordinate ->
                        mapInstance.move(to = MapPoint(coordinate.lat, coordinate.lng))
                    }
                }

                OrderStatus.Canceled -> {
                    val currentDestination = navController.currentDestination?.route ?: ""
                    if (!currentDestination.contains(ORDER_CANCELED_ROUTE)) {
                        withContext(Dispatchers.Default) {
                            vm.clearState()
                        }
                        scope.launch(Dispatchers.Main.immediate) {
                            navController.navigateToCanceledOrder()
                        }
                    }
                }

                OrderStatus.Completed -> {
                    if (navController.shouldNavigateToSheet(FEEDBACK_ROUTE, order.id)) {
                        scope.launch(Dispatchers.Main.immediate) {
                            navController.navigateToFeedbackSheet(orderId = order.id)
                        }
                    }
                }

                OrderStatus.InFetters -> {
                    if (navController.shouldNavigateToSheet(ON_THE_RIDE_ROUTE, order.id)) {
                        scope.launch(Dispatchers.Main.immediate) {
                            navController.navigateToOnTheRideSheet(orderId = order.id)
                        }
                    }

                    order.executor.coords.let { coordinate ->
                        mapInstance.move(to = MapPoint(coordinate.lat, coordinate.lng))
                    }
                }

                null -> {
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
                    if (navController.shouldNavigateToSheet(SEARCH_CAR_ROUTE, order.id)) {
                        order.taxi.routes.firstOrNull()?.coords?.let { coordinate ->
                            scope.launch(Dispatchers.Main.immediate) {
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
                    }

                    order.taxi.routes.firstOrNull()?.coords.let { coordinate ->
                        if (coordinate?.lat != null)
                            mapInstance.move(to = MapPoint(coordinate.lat, coordinate.lng))
                    }
                }
            }

            mapInstance.updateLocations(order?.taxi?.routes?.map {
                MapPoint(
                    it.coords.lat,
                    it.coords.lng
                )
            }.orEmpty())
        }

        // ✅ FIX 3: Optimize route updates with better caching
        LaunchedEffect(currentRoute, state.route, state.driverRoute) {
            val selectedRoute = when (currentRoute) {
                CLIENT_WAITING_ROUTE -> state.driverRoute
                else -> state.route
            }

            mapInstance.updateRoute(selectedRoute)

            if (selectedRoute.isEmpty()) {
                state.selectedLocation?.point?.let { mapInstance.animate(it) }
            } else if (state.selectedOrder?.status !in OrderStatus.nonInteractive) {
                // Delay expensive fit bounds operation
                scope.launch(Dispatchers.Default) {
                    delay(100) // Allow UI to settle
                    withContext(Dispatchers.Main) {
                        mapInstance.animateToFitBounds(selectedRoute)
                    }
                }
            }
        }

        // ✅ FIX 4: Optimize location processing with caching
        val processedLocations = remember(state.selectedLocation, state.destinations) {
            val start = state.selectedLocation?.point?.let { MapPoint(it.lat, it.lng) }
            val dest = state.destinations.mapNotNull {
                it.point?.let { point -> MapPoint(point.lat, point.lng) }
            }
            listOfNotNull(start) + dest
        }

        LaunchedEffect(processedLocations) {
            mapInstance.updateLocations(processedLocations)
        }

        LaunchedEffect(state.drivers) {
            mapInstance.updateDrivers(state.drivers)
        }
    }

    // ✅ FIX 5: Optimize multiple channel collections with debouncing
    LaunchedEffect(Unit) {
        supervisorScope {
            launch {
                vm.getMe()
            }

            launch {
                NoServiceSheetChannel.intentFlow
                    .debounce(50.milliseconds)
                    .collectLatest { intent ->
                        when (intent) {
                            is NoServiceSheetIntent.SetSelectedLocation -> {
                                intent.location.point?.let { point ->
                                    scope.launch(Dispatchers.Main.immediate) {
                                        map?.animate(to = point)
                                    }
                                }
                            }
                        }
                    }
            }

            launch {
                MainSheetChannel.intentFlow
                    .debounce(50.milliseconds)
                    .collectLatest { intent ->
                        when (intent) {
                            is OrderTaxiSheetIntent.SetSelectedLocation -> {
                                vm.updateState(state.copy(selectedLocation = intent.selectedLocation))
                                if (state.route.isEmpty()) {
                                    intent.selectedLocation.point?.let { point ->
                                        scope.launch(Dispatchers.Main) {
                                            while (map == null) delay(100)
                                            map?.animate(to = point)
                                        }
                                    }
                                }
                            }

                            is OrderTaxiSheetIntent.SetDestinations ->
                                vm.updateState(state.copy(destinations = intent.destinations))

                            is OrderTaxiSheetIntent.AddDestination ->
                                vm.updateState(state.copy(destinations = state.destinations + intent.destination))

                            is OrderTaxiSheetIntent.OrderCreated -> {
                                vm.setSelectedOrder(intent.order)
                                vm.updateState(state.copy(markerState = YallaMarkerState.Searching))
                            }

                            is OrderTaxiSheetIntent.SetTimeout ->
                                vm.updateState(
                                    state.copy(
                                        timeout = intent.timeout,
                                        drivers = intent.drivers
                                    )
                                )

                            is OrderTaxiSheetIntent.SetServiceState ->
                                vm.updateState(state.copy(hasServiceProvided = intent.available))

                            is PaymentMethodSheetIntent.OnAddNewCard -> onAddNewCard()

                            is MainSheetIntent.FooterIntent.Register -> onRegister()

                            else -> {}
                        }
                    }
            }

            launch {
                SearchCarSheetChannel.intentFlow
                    .debounce(100.milliseconds)
                    .collectLatest { intent ->
                        when (intent) {
                            is SearchCarSheetIntent.OnCancelled -> {
                                val orderId = state.selectedOrder?.id ?: intent.orderId

                                withContext(Dispatchers.Default) {
                                    vm.clearState()
                                }

                                scope.launch(Dispatchers.Main.immediate) {
                                    navController.navigateToMainSheet()
                                    orderId?.let { onCancel(it) }
                                }
                            }

                            is SearchCarSheetIntent.AddNewOrder -> {
                                withContext(Dispatchers.Default) {
                                    vm.clearState()
                                }
                                scope.launch(Dispatchers.Main.immediate) {
                                    navController.navigateToMainSheet()
                                }
                            }

                            is SearchCarSheetIntent.ZoomOut -> {
                                scope.launch(Dispatchers.Main.immediate) {
                                    map?.zoomOut()
                                }
                            }

                            else -> {}
                        }
                    }
            }

            launch {
                ClientWaitingSheetChannel.intentFlow
                    .debounce(50.milliseconds)
                    .collectLatest { intent ->
                        when (intent) {
                            is ClientWaitingSheetIntent.AddNewOrder -> {
                                withContext(Dispatchers.Default) {
                                    vm.clearState()
                                }
                                scope.launch(Dispatchers.Main.immediate) {
                                    navController.navigateToMainSheet()
                                }
                            }

                            is ClientWaitingSheetIntent.OnCancelled -> {
                                withContext(Dispatchers.Default) {
                                    vm.clearState()
                                }
                                intent.orderId?.let { onCancel(it) }
                            }

                            is ClientWaitingSheetIntent.UpdateRoute -> {
                                vm.updateState(state.copy(driverRoute = intent.route))
                            }

                            else -> {}
                        }
                    }
            }

            launch {
                DriverWaitingSheetChannel.intentFlow
                    .debounce(50.milliseconds)
                    .collectLatest { intent ->
                        when (intent) {
                            is DriverWaitingSheetIntent.OnCancelled -> {
                                scope.launch(Dispatchers.Main.immediate) {
                                    navController.navigateToMainSheet()
                                    intent.orderId?.let { onCancel(state.selectedOrder?.id ?: it) }
                                }
                                withContext(Dispatchers.Default) {
                                    vm.clearState()
                                }
                            }

                            is DriverWaitingSheetIntent.AddNewOrder -> {
                                withContext(Dispatchers.Default) {
                                    vm.clearState()
                                }
                                scope.launch(Dispatchers.Main.immediate) {
                                    navController.navigateToMainSheet()
                                }
                            }

                            else -> {}
                        }
                    }
            }

            launch {
                OnTheRideSheetChannel.intentFlow
                    .debounce(50.milliseconds)
                    .collectLatest { intent ->
                        if (intent is OnTheRideSheetIntent.AddNewOrder) {
                            withContext(Dispatchers.Default) {
                                vm.clearState()
                            }
                            scope.launch(Dispatchers.Main.immediate) {
                                navController.navigateToMainSheet()
                            }
                        }
                    }
            }

            launch {
                OrderCanceledSheetChannel.intentFlow
                    .debounce(50.milliseconds)
                    .collectLatest { intent ->
                        if (intent is OrderCanceledSheetIntent.StartNewOrder) {
                            withContext(Dispatchers.Default) {
                                vm.clearState()
                            }
                            scope.launch(Dispatchers.Main.immediate) {
                                navController.navigateToMainSheet()
                            }
                        }
                    }
            }
        }
    }

    LaunchedEffect(state.hasServiceProvided) {
        if (state.hasServiceProvided == true) {
            val currentDestination = navController.currentDestination?.route ?: ""
            if (!currentDestination.contains(MAIN_SHEET_ROUTE)) {
                val orderRelatedRoutes = listOf(
                    CLIENT_WAITING_ROUTE, DRIVER_WAITING_ROUTE, ON_THE_RIDE_ROUTE,
                    FEEDBACK_ROUTE, SEARCH_CAR_ROUTE, ORDER_CANCELED_ROUTE
                )
                val isInOrderFlow = orderRelatedRoutes.any { currentDestination.contains(it) }

                if (!isInOrderFlow) {
                    scope.launch(Dispatchers.Main.immediate) {
                        navController.navigateToMainSheet()
                    }
                }
            }
        } else if (state.hasServiceProvided == false) {
            val currentDestination = navController.currentDestination?.route ?: ""
            if (!currentDestination.contains(NO_SERVICE_ROUTE)) {
                scope.launch(Dispatchers.Main.immediate) {
                    navController.navigateToNoServiceSheet()
                }
            }
        }
    }

    MapDrawer(
        user = state.user,
        drawerState = drawerState,
        bonusAmount = state.user?.client?.balance.or0(),
        notificationsCount = state.notificationsCount,
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
                is MapDrawerIntent.Notifications -> onNotificationsClick()
                is MapDrawerIntent.Bonus -> onClickBonuses()
                is MapDrawerIntent.RegisterDevice -> onRegister()
            }
        },
        content = {
            map?.let { mapInstance ->
                MapScreen(
                    map = mapInstance,
                    state = state,
                    moveCameraButtonState = state.moveCameraButtonState,
                    hamburgerButtonState = hamburgerButtonState,
                    hasLocationPermission = permissionsGranted,
                    isLocationEnabled = isLocationEnabled,
                    navController = navController
                ) { intent ->
                    when (intent) {
                        is MapScreenIntent.MapOverlayIntent.ClickShowOrders -> {
                            vm.updateState(state.copy(isActiveOrdersSheetVisibility = true))
                        }

                        is MapScreenIntent.MapOverlayIntent.OnClickBonus -> {
                            MainSheetChannel.setBonusVisibility(true)
                        }

                        is MapScreenIntent.OnDismissActiveOrders -> {
                            vm.updateState(state.copy(isActiveOrdersSheetVisibility = false))
                        }

                        is MapScreenIntent.MapOverlayIntent.MoveToFirstLocation -> {
                            state.selectedLocation?.point?.let {
                                scope.launch(Dispatchers.Main.immediate) {
                                    mapInstance.animate(it)
                                }
                            }
                        }

                        is MapScreenIntent.MapOverlayIntent.AnimateToMyLocation -> {
                            scope.launch(Dispatchers.Main.immediate) {
                                mapInstance.animateToMyLocation()
                            }
                        }

                        is MapScreenIntent.MapOverlayIntent.MoveToMyRoute -> {
                            scope.launch(Dispatchers.Main.immediate) {
                                mapInstance.animateToFitBounds(state.route)
                            }
                        }

                        is MapScreenIntent.MapOverlayIntent.NavigateBack -> {
                            if (state.selectedOrder != null) {
                                vm.clearState()
                                scope.launch(Dispatchers.Main.immediate) {
                                    navController.navigateToMainSheet()
                                }
                            } else {
                                val destinations = state.destinations.toMutableList()
                                destinations.removeAt(destinations.lastIndex)
                                vm.updateState(state.copy(destinations = destinations))
                            }
                        }

                        is MapScreenIntent.MapOverlayIntent.OpenDrawer -> {
                            scope.launch {
                                drawerState.open()
                            }
                        }

                        is MapScreenIntent.SetShowingOrder -> {
                            vm.setSelectedOrder(intent.order)
                        }

                        is MapScreenIntent.MapOverlayIntent.OnMapReady -> {
                            val orderCoordinates =
                                state.selectedOrder?.taxi?.routes?.firstOrNull()?.coords
                            when {
                                orderCoordinates?.lat != null -> {
                                    mapInstance.move(
                                        to = MapPoint(
                                            orderCoordinates.lat,
                                            orderCoordinates.lng
                                        )
                                    )
                                }

                                state.selectedLocation?.point != null -> {
                                    state.selectedLocation?.point?.let { location ->
                                        mapInstance.move(to = location)
                                    }
                                }

                                else -> {
                                    mapInstance.moveToMyLocation()
                                }
                            }
                        }

                        MapScreenIntent.MapOverlayIntent.AskForPermission -> {
                            requestPermission(context, locationPermissionRequest)
                        }

                        MapScreenIntent.MapOverlayIntent.AskForEnable -> {
                            showEnableLocationSettings(context)
                        }
                    }
                }
            }
        }
    )

    if (state.loading || map == null) {
        LoadingDialog()
    }

    if (showPermissionDialog) {
        BaseDialog(
            title = stringResource(R.string.location_required),
            description = stringResource(R.string.location_required_body),
            actionText = stringResource(R.string.enable_loacation),
            onAction = {
                requestPermission(context, locationPermissionRequest)
            },
            onDismiss = {
                showPermissionDialog = false
            }
        )
    }
}

private fun NavController.shouldNavigateToSheet(
    routePattern: String,
    orderId: Int
): Boolean {
    val currentDestination = this.currentDestination
    val thisRoute = currentDestination?.route ?: ""

    if (!thisRoute.contains(routePattern)) {
        return true
    }

    val currentBackStackEntry = this.currentBackStackEntry
    val currentOrderId = currentBackStackEntry?.arguments?.getInt(ORDER_ID, -1) ?: -1

    return currentOrderId != orderId
}

private fun requestPermission(
    context: Context,
    requestLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
        (context as Activity),
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    if (shouldShowRationale) {
        requestLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    } else {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }
}

private suspend fun optimizedHandleSheetHeightChange(
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

fun showEnableLocationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(intent)
}

private fun checkLocation(
    context: Context,
    provideLocationState: (Boolean) -> Unit,
    providePermissionState: (Boolean) -> Unit,
    provideShowPermissionDialog: (Boolean) -> Unit
) {
    val finePermission = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val coarsePermission = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager

    provideLocationState(
        locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    )

    providePermissionState(
        finePermission == PackageManager.PERMISSION_GRANTED ||
                coarsePermission == PackageManager.PERMISSION_GRANTED
    )

    provideShowPermissionDialog(
        finePermission != PackageManager.PERMISSION_GRANTED ||
                coarsePermission != PackageManager.PERMISSION_GRANTED
    )
}