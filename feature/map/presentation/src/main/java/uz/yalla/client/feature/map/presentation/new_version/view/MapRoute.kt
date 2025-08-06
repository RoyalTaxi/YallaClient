package uz.yalla.client.feature.map.presentation.new_version.view

import android.content.IntentFilter
import android.location.LocationManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.maps.MapsIntent
import uz.yalla.client.core.common.maps.MapsViewModel
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.map.presentation.R
import uz.yalla.client.feature.map.presentation.new_version.intent.MapDrawerIntent
import uz.yalla.client.feature.map.presentation.new_version.intent.MapEffect
import uz.yalla.client.feature.map.presentation.new_version.intent.MapIntent
import uz.yalla.client.feature.map.presentation.new_version.model.MViewModel
import uz.yalla.client.feature.map.presentation.new_version.model.onIntent
import uz.yalla.client.feature.map.presentation.new_version.model.removeLastDestination
import uz.yalla.client.feature.map.presentation.new_version.model.setLocationEnabled
import uz.yalla.client.feature.map.presentation.new_version.model.setLocationGranted
import uz.yalla.client.feature.map.presentation.new_version.model.setPermissionDialog
import uz.yalla.client.feature.map.presentation.new_version.navigation.FromMap
import uz.yalla.client.feature.map.presentation.new_version.navigation.shouldNavigateToSheet
import uz.yalla.client.feature.map.presentation.new_version.utils.LocationServiceReceiver
import uz.yalla.client.feature.map.presentation.new_version.utils.checkLocation
import uz.yalla.client.feature.map.presentation.new_version.utils.requestPermission
import uz.yalla.client.feature.map.presentation.new_version.utils.showEnableLocationSettings
import uz.yalla.client.feature.order.presentation.cancel_reason.CANCEL_REASON_ROUTE
import uz.yalla.client.feature.order.presentation.cancel_reason.navigateToCancelReasonSheet
import uz.yalla.client.feature.order.presentation.cancel_reason.view.CancelReasonSheetChannel
import uz.yalla.client.feature.order.presentation.client_waiting.CLIENT_WAITING_ROUTE
import uz.yalla.client.feature.order.presentation.client_waiting.navigateToClientWaitingSheet
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingSheetChannel
import uz.yalla.client.feature.order.presentation.driver_waiting.DRIVER_WAITING_ROUTE
import uz.yalla.client.feature.order.presentation.driver_waiting.navigateToDriverWaitingSheet
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingSheetChannel
import uz.yalla.client.feature.order.presentation.feedback.FEEDBACK_ROUTE
import uz.yalla.client.feature.order.presentation.feedback.navigateToFeedbackSheet
import uz.yalla.client.feature.order.presentation.feedback.view.FeedbackSheetChannel
import uz.yalla.client.feature.order.presentation.main.MAIN_SHEET_ROUTE
import uz.yalla.client.feature.order.presentation.main.navigateToMainSheet
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import uz.yalla.client.feature.order.presentation.no_service.NO_SERVICE_ROUTE
import uz.yalla.client.feature.order.presentation.no_service.navigateToNoServiceSheet
import uz.yalla.client.feature.order.presentation.on_the_ride.ON_THE_RIDE_ROUTE
import uz.yalla.client.feature.order.presentation.on_the_ride.navigateToOnTheRideSheet
import uz.yalla.client.feature.order.presentation.on_the_ride.view.OnTheRideSheetChannel
import uz.yalla.client.feature.order.presentation.order_canceled.ORDER_CANCELED_ROUTE
import uz.yalla.client.feature.order.presentation.order_canceled.navigateToCanceledOrder
import uz.yalla.client.feature.order.presentation.order_canceled.view.OrderCanceledSheetChannel
import uz.yalla.client.feature.order.presentation.search.SEARCH_CAR_ROUTE
import uz.yalla.client.feature.order.presentation.search.navigateToSearchForCarBottomSheet
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetChannel


@Composable
fun MRoute(
    networkState: Boolean,
    onNavigate: (FromMap) -> Unit,
    mapsViewModel: MapsViewModel,
    viewModel: MViewModel = koinViewModel(parameters = { parametersOf(mapsViewModel) }),
    staticPreferences: StaticPreferences = koinInject()
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val activity = LocalActivity.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val statusBarHeight = WindowInsets.statusBars.getTop(density)
    val topPaddingDp = with(density) { statusBarHeight.toDp() }

    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        onResult = { granted -> viewModel.setPermissionDialog(!granted) }
    )

    val locationServiceReceiver = rememberUpdatedState(
        LocationServiceReceiver { isEnabled -> viewModel.setLocationEnabled(isEnabled) }
    )

    val activeCollectorRef = remember { mutableStateOf<Job?>(null) }

    BackHandler {
        when {
            state.destinations.isNotEmpty() -> viewModel.removeLastDestination()
            state.order == null -> activity?.moveTaskToBack(true)
            navController.currentDestination?.route == MAIN_SHEET_ROUTE -> {
                activity?.moveTaskToBack(true)
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) viewModel.onAppear()
            else if (event == Lifecycle.Event.ON_STOP) viewModel.onDisappear()
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        staticPreferences.processingOrderId?.let { orderId ->
            viewModel.onIntent(MapIntent.SetShowingOrderId(orderId))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effectFlow.collect { effect ->
            when (effect) {
                MapEffect.EnableLocation -> showEnableLocationSettings(context)
                MapEffect.GrantLocation -> requestPermission(context, locationPermissionRequest)
                MapEffect.NavigateToAddCard -> onNavigate(FromMap.ToAddNewCard)
                MapEffect.NavigateToRegister -> onNavigate(FromMap.ToRegister)
                is MapEffect.NavigateToCancelled -> {
                    if (navController.shouldNavigateToSheet(CANCEL_REASON_ROUTE, effect.orderId)) {
                        navController.navigateToCancelReasonSheet(effect.orderId)
                    }
                    activeCollectorRef.value = scope.launch {
                        CancelReasonSheetChannel.intentFlow.collectLatest { intent ->
                            viewModel.onIntent(intent)
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) checkLocation(
                context = context,
                provideLocationState = { state -> viewModel.setLocationEnabled(state) },
                providePermissionState = { state -> viewModel.setLocationGranted(state) },
                providePermissionVisibility = { visible -> viewModel.setPermissionDialog(visible) }
            )
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        val receiver = locationServiceReceiver.value
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(receiver, filter)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            context.unregisterReceiver(receiver)
            activeCollectorRef.value?.cancel()
        }
    }

    LaunchedEffect(topPaddingDp) {
        mapsViewModel.onIntent(MapsIntent.SetTopPadding(topPaddingDp))
    }

    LaunchedEffect(state.serviceAvailable) {
        if (state.serviceAvailable == true) {
            val currentDestination = navController.currentDestination?.route ?: ""
            if (!currentDestination.contains(MAIN_SHEET_ROUTE)) {
                val orderRelatedRoutes = listOf(
                    CLIENT_WAITING_ROUTE, DRIVER_WAITING_ROUTE, ON_THE_RIDE_ROUTE,
                    FEEDBACK_ROUTE, SEARCH_CAR_ROUTE, ORDER_CANCELED_ROUTE, CANCEL_REASON_ROUTE
                )
                val isInOrderFlow = orderRelatedRoutes.any { currentDestination.contains(it) }

                if (!isInOrderFlow) {
                    navController.navigateToMainSheet()
                }
            }
        } else if (state.serviceAvailable == false) {
            val currentDestination = navController.currentDestination?.route ?: ""
            if (!currentDestination.contains(NO_SERVICE_ROUTE)) {
                scope.launch(Dispatchers.Main.immediate) {
                    navController.navigateToNoServiceSheet()
                }
            }
        }
    }

    LaunchedEffect(state.order) {
        activeCollectorRef.value?.cancel()

        when (state.order?.status) {
            OrderStatus.Appointed -> {
                state.order?.id?.let { orderId ->
                    if (navController.shouldNavigateToSheet(CLIENT_WAITING_ROUTE, orderId)) {
                        navController.navigateToClientWaitingSheet(orderId = orderId)
                    }
                    activeCollectorRef.value = scope.launch {
                        ClientWaitingSheetChannel.intentFlow.collectLatest { intent ->
                            viewModel.onIntent(intent)
                        }
                    }
                }
            }

            OrderStatus.AtAddress -> {
                state.order?.id?.let { orderId ->
                    if (navController.shouldNavigateToSheet(DRIVER_WAITING_ROUTE, orderId)) {
                        navController.navigateToDriverWaitingSheet(orderId = orderId)
                    }
                    activeCollectorRef.value = scope.launch {
                        DriverWaitingSheetChannel.intentFlow.collectLatest { intent ->
                            viewModel.onIntent(intent)
                        }
                    }
                }
            }

            OrderStatus.InFetters -> {
                state.order?.id?.let { orderId ->
                    if (navController.shouldNavigateToSheet(ON_THE_RIDE_ROUTE, orderId)) {
                        navController.navigateToOnTheRideSheet(orderId = orderId)
                    }
                    activeCollectorRef.value = scope.launch {
                        OnTheRideSheetChannel.intentFlow.collectLatest { intent ->
                            viewModel.onIntent(intent)
                        }
                    }
                }
            }

            OrderStatus.Canceled -> {
                state.order?.id?.let { orderId ->
                    if (navController.shouldNavigateToSheet(ORDER_CANCELED_ROUTE, orderId)) {
                        navController.navigateToCanceledOrder()
                    }
                    activeCollectorRef.value = scope.launch {
                        OrderCanceledSheetChannel.intentFlow.collectLatest { intent ->
                            viewModel.onIntent(intent)
                        }
                    }
                }
            }

            OrderStatus.Completed -> {
                state.order?.id?.let { orderId ->
                    if (navController.shouldNavigateToSheet(FEEDBACK_ROUTE, orderId)) {
                        navController.navigateToFeedbackSheet(orderId = orderId)
                    }
                    activeCollectorRef.value = scope.launch {
                        FeedbackSheetChannel.intentFlow.collectLatest { intent ->
                            viewModel.onIntent(intent)
                        }
                    }
                }
            }

            null -> {
                if (state.orderId == null) {
                    navController.navigateToMainSheet()
                    activeCollectorRef.value = scope.launch {
                        MainSheetChannel.intentFlow.collectLatest { intent ->
                            viewModel.onIntent(intent)
                        }
                    }
                    delay(300)
                    mapsViewModel.onIntent(MapsIntent.AnimateToMyLocation(context))
                }
            }

            else -> {
                state.order?.id?.let { orderId ->
                    state.location?.point?.let { point ->
                        state.tariffId?.let { tariffId ->
                            if (navController.shouldNavigateToSheet(SEARCH_CAR_ROUTE, orderId)) {
                                navController.navigateToSearchForCarBottomSheet(
                                    orderId = orderId,
                                    point = point,
                                    tariffId = tariffId
                                )
                                viewModel.onIntent(MapIntent.MapOverlayIntent.MoveToFirstLocation)
                            }
                            activeCollectorRef.value = scope.launch {
                                SearchCarSheetChannel.intentFlow.collectLatest { intent ->
                                    viewModel.onIntent(intent)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.permissionDialogVisible) {
        BaseDialog(
            title = stringResource(R.string.location_required),
            description = stringResource(R.string.location_required_body),
            actionText = stringResource(R.string.enable_loacation),
            onAction = { requestPermission(context, locationPermissionRequest) },
            onDismiss = { viewModel.setPermissionDialog(false) }
        )
    }

    MScreen(
        state = state,
        networkState = networkState,
        navController = navController,
        onIntent = viewModel::onIntent,
        onDrawerIntent = { intent ->
            when (intent) {
                MapDrawerIntent.AboutTheApp -> onNavigate(FromMap.ToAboutApp)
                MapDrawerIntent.Bonus -> onNavigate(FromMap.ToBonuses)
                MapDrawerIntent.ContactUs -> onNavigate(FromMap.ToContactUs)
                MapDrawerIntent.MyPlaces -> onNavigate(FromMap.ToAddresses)
                MapDrawerIntent.Notifications -> onNavigate(FromMap.ToNotifications)
                MapDrawerIntent.OrdersHistory -> onNavigate(FromMap.ToOrderHistory)
                MapDrawerIntent.PaymentType -> onNavigate(FromMap.ToPaymentType)
                MapDrawerIntent.Profile -> onNavigate(FromMap.ToProfile)
                MapDrawerIntent.RegisterDevice -> onNavigate(FromMap.ToRegister)
                MapDrawerIntent.Settings -> onNavigate(FromMap.ToSettings)
                is MapDrawerIntent.BecomeADriver -> onNavigate(
                    FromMap.ToBecomeDriver(
                        title = intent.title,
                        url = intent.url
                    )
                )

                is MapDrawerIntent.InviteFriend -> onNavigate(
                    FromMap.ToInviteFriend(
                        intent.title,
                        intent.url
                    )
                )
            }
        }
    )
}