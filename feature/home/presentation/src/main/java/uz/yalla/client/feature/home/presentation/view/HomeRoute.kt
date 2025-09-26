package uz.yalla.client.feature.home.presentation.view

import android.content.IntentFilter
import android.location.LocationManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.lifecycle.MakeBridge
import uz.yalla.client.feature.home.presentation.R
import uz.yalla.client.feature.home.presentation.intent.HomeDrawerIntent.AboutTheApp
import uz.yalla.client.feature.home.presentation.intent.HomeDrawerIntent.BecomeADriver
import uz.yalla.client.feature.home.presentation.intent.HomeDrawerIntent.Bonus
import uz.yalla.client.feature.home.presentation.intent.HomeDrawerIntent.ContactUs
import uz.yalla.client.feature.home.presentation.intent.HomeDrawerIntent.InviteFriend
import uz.yalla.client.feature.home.presentation.intent.HomeDrawerIntent.MyPlaces
import uz.yalla.client.feature.home.presentation.intent.HomeDrawerIntent.Notifications
import uz.yalla.client.feature.home.presentation.intent.HomeDrawerIntent.OrdersHistory
import uz.yalla.client.feature.home.presentation.intent.HomeDrawerIntent.PaymentType
import uz.yalla.client.feature.home.presentation.intent.HomeDrawerIntent.Profile
import uz.yalla.client.feature.home.presentation.intent.HomeDrawerIntent.RegisterDevice
import uz.yalla.client.feature.home.presentation.intent.HomeDrawerIntent.Settings
import uz.yalla.client.feature.home.presentation.intent.HomeEffect
import uz.yalla.client.feature.home.presentation.intent.HomeIntent
import uz.yalla.client.feature.home.presentation.model.HomeViewModel
import uz.yalla.client.feature.home.presentation.model.onIntent
import uz.yalla.client.feature.home.presentation.model.removeLastDestination
import uz.yalla.client.feature.home.presentation.model.setLocationEnabled
import uz.yalla.client.feature.home.presentation.model.setLocationGranted
import uz.yalla.client.feature.home.presentation.model.setPermissionDialog
import uz.yalla.client.feature.home.presentation.navigation.FromMap
import uz.yalla.client.feature.home.presentation.navigation.FromMap.ToAboutApp
import uz.yalla.client.feature.home.presentation.navigation.FromMap.ToAddNewCard
import uz.yalla.client.feature.home.presentation.navigation.FromMap.ToAddresses
import uz.yalla.client.feature.home.presentation.navigation.FromMap.ToBecomeDriver
import uz.yalla.client.feature.home.presentation.navigation.FromMap.ToBonuses
import uz.yalla.client.feature.home.presentation.navigation.FromMap.ToContactUs
import uz.yalla.client.feature.home.presentation.navigation.FromMap.ToInviteFriend
import uz.yalla.client.feature.home.presentation.navigation.FromMap.ToNotifications
import uz.yalla.client.feature.home.presentation.navigation.FromMap.ToOrderHistory
import uz.yalla.client.feature.home.presentation.navigation.FromMap.ToPaymentType
import uz.yalla.client.feature.home.presentation.navigation.FromMap.ToProfile
import uz.yalla.client.feature.home.presentation.navigation.FromMap.ToRegister
import uz.yalla.client.feature.home.presentation.navigation.FromMap.ToSettings
import uz.yalla.client.feature.home.presentation.navigation.OrderSheet.CancelReason
import uz.yalla.client.feature.home.presentation.navigation.OrderSheet.Canceled
import uz.yalla.client.feature.home.presentation.navigation.OrderSheet.ClientWaiting
import uz.yalla.client.feature.home.presentation.navigation.OrderSheet.DriverWaiting
import uz.yalla.client.feature.home.presentation.navigation.OrderSheet.Feedback
import uz.yalla.client.feature.home.presentation.navigation.OrderSheet.Main
import uz.yalla.client.feature.home.presentation.navigation.OrderSheet.NoService
import uz.yalla.client.feature.home.presentation.navigation.OrderSheet.OnTheRide
import uz.yalla.client.feature.home.presentation.navigation.OrderSheet.Search
import uz.yalla.client.feature.home.presentation.sheets.ActiveOrdersBottomSheet
import uz.yalla.client.feature.home.presentation.utils.LocationServiceReceiver
import uz.yalla.client.feature.home.presentation.utils.checkLocation
import uz.yalla.client.feature.home.presentation.utils.requestPermission
import uz.yalla.client.feature.home.presentation.utils.showEnableLocationSettings
import uz.yalla.client.feature.order.presentation.cancel_reason.view.CancelReasonSheetChannel
import uz.yalla.client.feature.order.presentation.client_waiting.view.ClientWaitingSheetChannel
import uz.yalla.client.feature.order.presentation.driver_waiting.view.DriverWaitingSheetChannel
import uz.yalla.client.feature.order.presentation.feedback.view.FeedbackSheetChannel
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel
import uz.yalla.client.feature.order.presentation.no_service.view.NoServiceSheetChannel
import uz.yalla.client.feature.order.presentation.on_the_ride.view.OnTheRideSheetChannel
import uz.yalla.client.feature.order.presentation.order_canceled.view.OrderCanceledSheetChannel
import uz.yalla.client.feature.order.presentation.search.view.SearchCarSheetChannel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    networkState: Boolean,
    onNavigate: (FromMap) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val activity = LocalActivity.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val scope = rememberCoroutineScope()
    val ordersSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val statusBarHeight = WindowInsets.statusBars.getTop(density)
    val topPaddingDp = with(density) { statusBarHeight.toDp() }

    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val sheet by viewModel.sheetFlow.collectAsStateWithLifecycle()
    val isMapReady by viewModel.mapViewModel.isMapReady.collectAsStateWithLifecycle(true)

    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        onResult = { granted -> viewModel.setPermissionDialog(!granted) }
    )

    val locationServiceReceiver = rememberUpdatedState(
        LocationServiceReceiver { isEnabled -> viewModel.setLocationEnabled(isEnabled) }
    )

    var activeCollectorRef by remember { mutableStateOf<Job?>(null) }

    BackHandler {
        when {
            state.destinations.isNotEmpty() -> viewModel.removeLastDestination()
            state.order == null -> activity?.moveTaskToBack(true)
            sheet is Main -> {
                activity?.moveTaskToBack(true)
            }
        }
    }

    lifecycleOwner.MakeBridge(viewModel)

    viewModel.collectSideEffect { effect ->
        when (effect) {
            HomeEffect.EnableLocation -> showEnableLocationSettings(context)
            HomeEffect.GrantLocation -> requestPermission(context, locationPermissionRequest)
            HomeEffect.NavigateToAddCard -> onNavigate(ToAddNewCard)
            HomeEffect.NavigateToRegister -> onNavigate(ToRegister)
            is HomeEffect.NavigateToCancelled -> {
                viewModel.setSheet(CancelReason(effect.orderId))
            }

            is HomeEffect.ActiveOrderSheetState -> {
                if (effect.visible) {
                    scope.launch { ordersSheetState.show() }
                } else {
                    scope.launch { ordersSheetState.hide() }
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
        val appContext = context.applicationContext
        appContext.registerReceiver(receiver, filter)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            runCatching { appContext.unregisterReceiver(receiver) }
            activeCollectorRef?.cancel()
        }
    }

    LaunchedEffect(topPaddingDp) {
        viewModel.onIntent(HomeIntent.SetTopPadding(topPaddingDp))
    }

    LaunchedEffect(sheet) {
        activeCollectorRef?.cancel()

        if (activeCollectorRef?.isCancelled == false) return@LaunchedEffect

        activeCollectorRef = when (sheet) {
            is Main -> scope.launch {
                MainSheetChannel.intentFlow.collectLatest(viewModel::onIntent)
            }

            is Search -> scope.launch {
                SearchCarSheetChannel.intentFlow.collectLatest(viewModel::onIntent)
            }

            is ClientWaiting -> scope.launch {
                ClientWaitingSheetChannel.intentFlow.collectLatest(viewModel::onIntent)
            }

            is DriverWaiting -> scope.launch {
                DriverWaitingSheetChannel.intentFlow.collectLatest(viewModel::onIntent)
            }

            is OnTheRide -> scope.launch {
                OnTheRideSheetChannel.intentFlow.collectLatest(viewModel::onIntent)
            }

            is Canceled -> scope.launch {
                OrderCanceledSheetChannel.intentFlow.collectLatest(viewModel::onIntent)
            }

            is Feedback -> scope.launch {
                FeedbackSheetChannel.intentFlow.collectLatest(viewModel::onIntent)
            }

            is NoService -> scope.launch {
                NoServiceSheetChannel.intentFlow.collectLatest(viewModel::onIntent)
            }

            is CancelReason -> scope.launch {
                CancelReasonSheetChannel.intentFlow.collectLatest(viewModel::onIntent)
            }

            null -> null
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

    HomeScreen(
        state = state,
        networkState = networkState,
        isDrawerOpen = state.isDrawerOpen,
        isOrderCanceledVisible = sheet is Canceled,
        onIntent = viewModel::onIntent,
        onDrawerIntent = { intent ->
            when (intent) {
                AboutTheApp -> onNavigate(ToAboutApp)
                Bonus -> onNavigate(ToBonuses)
                ContactUs -> onNavigate(ToContactUs)
                MyPlaces -> onNavigate(ToAddresses)
                Notifications -> onNavigate(ToNotifications)
                OrdersHistory -> onNavigate(ToOrderHistory)
                PaymentType -> onNavigate(ToPaymentType)
                Profile -> onNavigate(ToProfile)
                RegisterDevice -> onNavigate(ToRegister)
                Settings -> onNavigate(ToSettings)
                is BecomeADriver -> onNavigate(
                    ToBecomeDriver(
                        title = intent.title,
                        url = intent.url
                    )
                )

                is InviteFriend -> onNavigate(
                    ToInviteFriend(
                        intent.title,
                        intent.url
                    )
                )

            }
        }
    )

    if (state.ordersSheetVisible) {
        ActiveOrdersBottomSheet(
            sheetState = ordersSheetState,
            orders = state.orders,
            onSelectOrder = { order ->
                scope.launch { ordersSheetState.hide() }
                viewModel.onIntent(HomeIntent.SetShowingOrder(order))
            },
            onDismissRequest = {
                scope.launch { ordersSheetState.hide() }
                viewModel.onIntent(HomeIntent.OnDismissActiveOrders)
            }
        )
    }

    OrderSheetHost(
        sheet = sheet,
        onIntent = viewModel::onIntent
    )

    if (isMapReady.not()) LoadingDialog()
}
