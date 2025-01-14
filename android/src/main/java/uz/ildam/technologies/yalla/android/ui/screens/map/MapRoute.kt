package uz.ildam.technologies.yalla.android.ui.screens.map

import android.Manifest
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.android.ui.dialogs.LoadingDialog
import uz.ildam.technologies.yalla.android.ui.sheets.SheetValue
import uz.ildam.technologies.yalla.android.utils.rememberPermissionState
import uz.ildam.technologies.yalla.core.data.enums.MapType
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.core.domain.model.MapPoint
import uz.ildam.technologies.yalla.feature.order.domain.model.response.order.OrderStatus
import uz.yalla.client.feature.core.map.ConcreteGisMap
import uz.yalla.client.feature.core.map.ConcreteGoogleMap
import uz.yalla.client.feature.core.map.MapStrategy
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    becomeDriverClick: (String, String) -> Unit,
    inviteFriendClick: (String, String) -> Unit,
    vm: MapViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val uiState by vm.uiState.collectAsState()
    var loading by remember { mutableStateOf(false) }

    val searchLocationState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val destinationsState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val tariffState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val optionsState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val confirmCancellationState = rememberModalBottomSheetState(confirmValueChange = { false })
    val selectPaymentMethodState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val orderCommentState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val activeOrdersState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val scaffoldState = rememberBottomSheetScaffoldState(
        sheetState = rememberBottomSheetState(
            confirmValueChange = { false },
            initialValue = SheetValue.Expanded,
            defineValues = {
                SheetValue.Collapsed at contentHeight
                SheetValue.PartiallyExpanded at contentHeight
                SheetValue.Expanded at contentHeight
            }
        )
    )

    val permissionsGranted by rememberPermissionState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val map: MapStrategy by remember {
        mutableStateOf(
            when (AppPreferences.mapType) {
                MapType.Google -> ConcreteGoogleMap()
                MapType.Gis -> ConcreteGisMap()
            }
        )
    }

    val bottomSheetHandler by remember {
        mutableStateOf(
            MapBottomSheetHandler(
                context = context,
                scope = scope,
                searchLocationState = searchLocationState,
                destinationsState = destinationsState,
                tariffState = tariffState,
                optionsState = optionsState,
                confirmCancellationState = confirmCancellationState,
                selectPaymentMethodState = selectPaymentMethodState,
                orderCommentState = orderCommentState,
                activeOrdersState = activeOrdersState,
                viewModel = vm
            )
        )
    }

    val sheetHandler = remember {
        MapSheetHandler(
            viewModel = vm,
            bottomSheetHandler = bottomSheetHandler
        )
    }

    BackHandler {
        if (drawerState.isOpen) scope.launch {
            drawerState.close()
        }
        else if (uiState.route.isNotEmpty()) {
            vm.setDestinations(emptyList())
            uiState.selectedLocation?.point?.let { there -> map.animate(to = there) }
        } else if (uiState.selectedDriver == null) {
            (context as? Activity)?.finish()
        }
    }

    LaunchedEffect(Unit) {
        launch {
            while (true) {
                vm.getActiveOrders()
                delay(5.seconds)
                yield()
            }
        }

        launch {
            vm.actionState.collectLatest { action ->
                when (action) {
                    is MapActionState.AddressIdLoaded -> vm.fetchTariffs(action.id.toInt())
                    is MapActionState.PolygonLoaded -> vm.getAddressDetails(map.mapPoint.value)
                    is MapActionState.TariffsLoaded -> vm.getTimeout(map.mapPoint.value)
                    is MapActionState.AddressNameLoaded -> vm.setSelectedLocation(name = action.name)
                    else -> {}
                }
            }
        }
    }

    LaunchedEffect(uiState.route) {
        launch {
            map.updateRoute(uiState.route)
            if (uiState.route.isEmpty()) map.moveToMyLocation()
            else map.moveToFitBounds(uiState.route)
        }
    }

    LaunchedEffect(uiState.selectedLocation, uiState.destinations) {
        launch {
            val start = uiState.selectedLocation?.point?.let { MapPoint(it.lat, it.lng) }
            val dest = uiState.destinations.mapNotNull {
                it.point?.let { point -> MapPoint(point.lat, point.lng) }
            }
            val locations = listOfNotNull(start) + dest
            map.updateLocations(locations)
        }
    }


    LaunchedEffect(uiState.drivers) { launch { map.updateDrivers(uiState.drivers) } }

    LaunchedEffect(map.isMarkerMoving.value) {
        launch {
            if (uiState.route.isEmpty() && uiState.selectedDriver?.status != OrderStatus.Appointed) {
                if (map.isMarkerMoving.value) vm.setNotFoundState()
                else vm.getAddressDetails(map.mapPoint.value)
            }
        }
    }

    LaunchedEffect(uiState.showingOrderId) {
        launch {
            while (uiState.showingOrderId != null) {
                vm.getShow()
                delay(5.seconds)
                yield()
            }
        }
    }

    LaunchedEffect(uiState.selectedDriver?.status) {
        launch {
            uiState.selectedDriver?.status?.let { map.updateOrderStatus(it) }
            when (uiState.selectedDriver?.status) {
                OrderStatus.Appointed -> sheetHandler.showClientWaiting()
                OrderStatus.AtAddress -> sheetHandler.showDriverWaiting()
                OrderStatus.Canceled -> sheetHandler.showOrderTaxi()
                OrderStatus.InFetters -> sheetHandler.showOnTheRide()
                OrderStatus.Completed -> sheetHandler.showFeedback()
                OrderStatus.New -> {
                    uiState.selectedLocation?.point?.let { there -> map.move(there) }
                    sheetHandler.showSearchCars()
                }

                OrderStatus.NonStopSending -> {
                    uiState.selectedLocation?.point?.let { there -> map.move(there) }
                    sheetHandler.showSearchCars()
                }

                OrderStatus.Sending -> {
                    uiState.selectedLocation?.point?.let { there -> map.move(there) }
                    sheetHandler.showSearchCars()
                }

                OrderStatus.UserSending -> {
                    uiState.selectedLocation?.point?.let { there -> map.move(there) }
                    sheetHandler.showSearchCars()
                }

                null -> sheetHandler.showOrderTaxi()
            }
        }
    }

    LaunchedEffect(uiState.selectedDriver) {
        launch {
            if (uiState.selectedDriver != null) loading = false
            if (uiState.selectedDriver?.status == OrderStatus.InFetters)
                uiState.selectedDriver?.let { driver ->
                    val driverPosition = MapPoint(
                        lat = driver.executor.coords.lat,
                        lng = driver.executor.coords.lng
                    )
                    map.animate(to = driverPosition)
                }
        }
    }

    LaunchedEffect(uiState.selectedOrder) {
        launch {
            if (uiState.selectedOrder == null) {
                vm.setSelectedDriver(driver = null)
            }
        }
    }

    if (!permissionsGranted) {
        onPermissionDenied()
        return
    }

    MapDrawer(
        drawerState = drawerState,
        uiState = uiState,
        onIntent = { intent ->
            when (intent) {
                is MapDrawerIntent.Profile -> onProfileClick()
                is MapDrawerIntent.OrdersHistory -> onOrderHistoryClick()
                is MapDrawerIntent.PaymentType -> onPaymentTypeClick()
                is MapDrawerIntent.MyPlaces -> onAddressesClick()
                is MapDrawerIntent.Settings -> onSettingsClick()
                is MapDrawerIntent.AboutTheApp -> onAboutAppClick()
                is MapDrawerIntent.ContactUs -> onContactUsClick()
                is MapDrawerIntent.BecomeADriver -> becomeDriverClick(intent.title, intent.url)
                is MapDrawerIntent.InviteFriend -> inviteFriendClick(intent.title, intent.url)
            }
        },
        content = {
            MapScreen(
                uiState = uiState,
                map = map,
                isLoading = map.isMarkerMoving.value && uiState.route.isEmpty(),
                scaffoldState = scaffoldState,
                mapSheetHandler = sheetHandler,
                currentLatLng = map.mapPoint,
                onCreateOrder = { loading = true },
                mapBottomSheetHandler = bottomSheetHandler,
                activeOrdersState = activeOrdersState,
                onIntent = { intent ->
                    when (intent) {
                        is MapIntent.MoveToMyLocation -> map.animateToMyLocation()
                        is MapIntent.MoveToMyRoute -> {
                            vm.updateCameraButton(MoveCameraButtonState.FirstLocation)
                            map.animateToFitBounds(uiState.route)
                        }

                        is MapIntent.MoveToFirstLocation -> {
                            vm.updateCameraButton(MoveCameraButtonState.MyRouteView)
                            map.animate(uiState.route.first())
                        }

                        is MapIntent.OpenDrawer -> scope.launch { drawerState.open() }
                        is MapIntent.DiscardOrder -> vm.setDestinations(emptyList())
                    }
                }
            )
        }
    )

    bottomSheetHandler.Sheets(
        uiState = uiState,
        map = map,
        onAddNewCard = onAddNewCard,
        onCancel = {
            uiState.showingOrderId?.let { vm.cancelRide(it) }
            onCancel()
        }
    )

    if (loading) LoadingDialog()
}