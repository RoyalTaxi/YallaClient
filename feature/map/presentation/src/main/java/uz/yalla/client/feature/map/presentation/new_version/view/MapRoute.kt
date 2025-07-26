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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.maps.MapsIntent
import uz.yalla.client.core.common.maps.MapsViewModel
import uz.yalla.client.feature.map.presentation.R
import uz.yalla.client.feature.map.presentation.new_version.intent.MapDrawerIntent
import uz.yalla.client.feature.map.presentation.new_version.intent.MapEffect
import uz.yalla.client.feature.map.presentation.new_version.model.MViewModel
import uz.yalla.client.feature.map.presentation.new_version.model.onIntent
import uz.yalla.client.feature.map.presentation.new_version.model.removeLastDestination
import uz.yalla.client.feature.map.presentation.new_version.model.setLocationEnabled
import uz.yalla.client.feature.map.presentation.new_version.model.setLocationGranted
import uz.yalla.client.feature.map.presentation.new_version.model.setPermissionDialog
import uz.yalla.client.feature.map.presentation.new_version.navigation.FromMap
import uz.yalla.client.feature.map.presentation.new_version.utils.LocationServiceReceiver
import uz.yalla.client.feature.map.presentation.new_version.utils.checkLocation
import uz.yalla.client.feature.map.presentation.new_version.utils.requestPermission
import uz.yalla.client.feature.map.presentation.new_version.utils.showEnableLocationSettings
import uz.yalla.client.feature.order.presentation.main.view.MainSheetChannel

@Composable
fun MRoute(
    networkState: Boolean,
    onNavigate: (FromMap) -> Unit,
    viewModel: MViewModel = koinViewModel(),
    mapsViewModel: MapsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val activity = LocalActivity.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
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

    BackHandler {
        when {
            state.destinations.isNotEmpty() -> viewModel.removeLastDestination()
            state.order == null -> activity?.moveTaskToBack(true)
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
        }
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            MapEffect.EnableLocation -> showEnableLocationSettings(context)
            MapEffect.GrantLocation -> requestPermission(context, locationPermissionRequest)
        }
    }

    LaunchedEffect(topPaddingDp) {
        mapsViewModel.onIntent(MapsIntent.SetTopPadding(topPaddingDp))
    }

    DisposableEffect(Unit) {
        scope.launch {
            viewModel.onAppear()
            mapsViewModel.onAppear()
        }
        onDispose {
            viewModel.onDisappear()
            mapsViewModel.onDisappear()
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            MainSheetChannel.intentFlow.collectLatest(viewModel::onIntent)
        }
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

    if (state.permissionDialogVisible) {
        BaseDialog(
            title = stringResource(R.string.location_required),
            description = stringResource(R.string.location_required_body),
            actionText = stringResource(R.string.enable_loacation),
            onAction = { requestPermission(context, locationPermissionRequest) },
            onDismiss = { viewModel.setPermissionDialog(false) }
        )
    }
}