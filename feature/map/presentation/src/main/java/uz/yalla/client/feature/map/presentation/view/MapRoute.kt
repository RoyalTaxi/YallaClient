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
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.map.ConcreteGisMap
import uz.yalla.client.core.common.map.ConcreteGoogleMap
import uz.yalla.client.core.common.map.MapStrategy
import uz.yalla.client.core.common.state.rememberPermissionState
import uz.yalla.client.core.data.enums.MapType
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.core.domain.model.SelectedLocation
import uz.yalla.client.feature.map.presentation.components.marker.YallaMarkerState
import uz.yalla.client.feature.map.presentation.model.MapViewModel
import uz.yalla.client.feature.map.presentation.view.drawer.MapDrawer
import uz.yalla.client.feature.map.presentation.view.drawer.MapDrawerIntent
import uz.yalla.client.feature.order.presentation.main.view.MainBottomSheetIntent
import uz.yalla.client.feature.order.presentation.main.view.MainSheet
import uz.yalla.client.feature.order.presentation.search.navigateToSearchForCarBottomSheet
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheet
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetIntent
import kotlin.time.Duration.Companion.seconds

@Composable
fun MapRoute(
    onPermissionDenied: () -> Unit,
    onProfileClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onPaymentTypeClick: () -> Unit,
    onAddressesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCancel: () -> Unit,
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
    var markerState by remember { mutableStateOf<YallaMarkerState>(YallaMarkerState.LOADING) }
    val map: MapStrategy by remember {
        mutableStateOf(
            when (AppPreferences.mapType) {
                MapType.Google -> ConcreteGoogleMap()
                MapType.Gis -> ConcreteGisMap()
            }
        )
    }
    val mapPoint by map.mapPoint
    val navController = rememberNavController()


    BackHandler {
        when {
            drawerState.isOpen -> scope.launch { drawerState.close() }

            state.route.isNotEmpty() -> {
                MainSheet.setDestination(emptyList())
                state.selectedLocation?.point?.let { there -> map.animate(to = there) }
            }

            state.selectedOrder == null -> (context as? Activity)?.finish()
        }
    }

    LaunchedEffect(permissionsGranted) {
        if (!permissionsGranted) onPermissionDenied()
    }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            while (true) {
                vm.getActiveOrders()
                delay(5.seconds)
                yield()
            }
        }

        launch(Dispatchers.IO) {
            vm.getPolygon()
            vm.getMe()
        }

        launch(Dispatchers.Main) {
            map.isMarkerMoving.collectLatest { isMarkerMoving ->
                if (state.route.isEmpty() && state.selectedOrder?.status == null) {
                    if (isMarkerMoving) vm.setStateToNotFound()
                    else {
                        vm.getAddressName(mapPoint)?.let { place ->
                            vm.updateState(
                                state.copy(
                                    selectedLocation = SelectedLocation(
                                        name = place.displayName,
                                        point = mapPoint,
                                        addressId = place.id
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }

        launch(Dispatchers.Main) {
            MainSheet.intentFlow.collectLatest { intent ->
                when (intent) {
                    is MainBottomSheetIntent.OrderTaxiBottomSheetIntent.AddNewDestinationClick -> TODO()
                    is MainBottomSheetIntent.OrderTaxiBottomSheetIntent.CurrentLocationClick -> TODO()
                    is MainBottomSheetIntent.OrderTaxiBottomSheetIntent.DestinationClick -> TODO()
                    is MainBottomSheetIntent.OrderTaxiBottomSheetIntent.OrderCreated -> {
                        vm.updateState(state.copy(showingOrderId = intent.orderId))
                        navController.navigateToSearchForCarBottomSheet(
                            tariffId = state.selectedTariffId.or0(),
                            point = mapPoint
                        )
                    }

                    is MainBottomSheetIntent.TariffInfoBottomSheetIntent.ClickComment -> {}
                    else -> {}
                }
            }
        }

        launch(Dispatchers.Main) {
            SearchCarSheet.intentFlow.collectLatest { intent ->
                when (intent) {
                    is SearchCarSheetIntent.OnCancelled -> {
                        onCancel()
                    }

                    is SearchCarSheetIntent.OnFoundCars -> {
                        // TODO: update cars
                    }

                    is SearchCarSheetIntent.SetSheetHeight -> {
                        vm.updateState(state.copy(sheetHeight = intent.height))
                    }
                }
            }
        }
    }

    LaunchedEffect(state.selectedLocation, state.selectedOrder) {
        launch(Dispatchers.Main) {
            markerState = when {
                state.selectedLocation == null -> YallaMarkerState.LOADING
                state.selectedOrder?.status in OrderStatus.nonInteractive -> YallaMarkerState.Searching(
                    title = state.selectedLocation?.name
                )

                else -> YallaMarkerState.IDLE(
                    title = state.selectedLocation?.name,
                    timeout = state.timeout
                )
            }
        }
    }

    LaunchedEffect(state.route) {
        launch(Dispatchers.Main) {
            map.updateRoute(state.route)
            if (state.route.isEmpty()) state.selectedLocation?.point?.let { map.animate(it) }
            else map.animateToFitBounds(state.route)
        }
    }

    LaunchedEffect(state.selectedLocation, state.destinations) {
        launch(Dispatchers.Main) {
            val start = state.selectedLocation?.point?.let {
                MapPoint(it.lat, it.lng)
            }
            val dest = state.destinations.mapNotNull {
                it.point?.let { point -> MapPoint(point.lat, point.lng) }
            }
            val locations = listOfNotNull(start) + dest
            map.updateLocations(locations)
        }
    }


    LaunchedEffect(state.drivers) {
        launch(Dispatchers.Main) {
            map.updateDrivers(state.drivers)
        }
    }

    LaunchedEffect(state.selectedOrder) {
        launch(Dispatchers.Main) {
            state.selectedOrder?.let { map.updateDriver(it.executor) }
        }
    }

    LaunchedEffect(state.showingOrderId) {
        launch(Dispatchers.IO) {
            while (state.showingOrderId != null) {
                vm.getShowOrder()
                delay(5.seconds)
                yield()
            }
        }
    }

    LaunchedEffect(state.selectedOrder?.status) {
        // Navigate to appropriate sheet
    }

    LaunchedEffect(state.selectedOrder) {
        launch(Dispatchers.Main) {
            if (state.selectedOrder != null) vm.updateState(state.copy(loading = false))
            if (state.selectedOrder?.status == OrderStatus.InFetters)
                state.selectedOrder?.let { driver ->
                    val driverPosition = MapPoint(
                        lat = driver.executor.coords.lat,
                        lng = driver.executor.coords.lng
                    )
                    map.animate(to = driverPosition)
                }
        }
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
                markerState = markerState,
                moveCameraButtonState = state.moveCameraButtonState,
                hamburgerButtonState = hamburgerButtonState,
                navController = navController
            ) { intent ->
                when (intent) {
                    is MapScreenIntent.MapOverlayIntent.ClickShowOrders -> {
                        
                    }

                    is MapScreenIntent.MapOverlayIntent.MoveToFirstLocation -> {
                        state.selectedLocation?.point?.let {
                            map.animate(it)
                        }
                    }

                    is MapScreenIntent.MapOverlayIntent.MoveToMyLocation -> {
                        map.animateToMyLocation()
                    }

                    is MapScreenIntent.MapOverlayIntent.MoveToMyRoute -> {
                        map.animateToFitBounds(state.route)
                    }

                    is MapScreenIntent.MapOverlayIntent.NavigateBack -> {

                    }

                    is MapScreenIntent.MapOverlayIntent.OpenDrawer -> {
                        scope.launch(Dispatchers.Main) {
                            drawerState.open()
                        }
                    }

                    is MapScreenIntent.SetSheetHeight -> {
                        vm.updateState(state.copy(sheetHeight = intent.height))
                    }
                }
            }
        }
    )

    if (state.loading) LoadingDialog()
}