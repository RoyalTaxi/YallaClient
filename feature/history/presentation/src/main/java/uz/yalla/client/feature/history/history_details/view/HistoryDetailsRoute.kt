package uz.yalla.client.feature.history.history_details.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.map.ConcreteGoogleMap
import uz.yalla.client.core.common.map.ConcreteLibreMap
import uz.yalla.client.core.common.map.MapStrategy
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.MapType
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.history.R
import uz.yalla.client.feature.history.history_details.model.HistoryDetailsViewModel

@Composable
internal fun HistoryDetailsRoute(
    onNavigateBack: () -> Unit,
    orderId: Int,
    vm: HistoryDetailsViewModel = koinViewModel()
) {
    val prefs = koinInject<AppPreferences>()

    val uiState by vm.uiState.collectAsState()
    val loading by vm.loading.collectAsState()

    val showErrorDialog by vm.showErrorDialog.collectAsState()
    val currentErrorMessageId by vm.currentErrorMessageId.collectAsState()

    val mapType by prefs.mapType.collectAsState(initial = null)

    val map: MapStrategy? = remember(mapType) {
        mapType?.let { type ->
            when (type) {
                MapType.Google -> ConcreteGoogleMap()
                MapType.Gis -> ConcreteGoogleMap()
                MapType.Libre -> ConcreteLibreMap()
            }
        }
    }

    fun updateRoute() {
        map?.let { mapInstance ->
            val routes = uiState.orderDetails?.taxi?.routes ?: return
            if (routes.isEmpty()) return

            val routePoints = routes.map { route ->
                route.cords.let { MapPoint(it.lat, it.lng) }
            }
            if (routePoints.isEmpty()) return

            if (routePoints.size == 1) {
                val singlePoint = routePoints.first()
                mapInstance.updateRoute(listOf(singlePoint))
                mapInstance.updateOrderStatus(OrderStatus.Appointed)
                mapInstance.move(singlePoint)
            } else {
                mapInstance.updateRoute(routePoints)
                mapInstance.moveToFitBounds(routePoints)
            }
            mapInstance.updateLocations(routePoints)
        }
    }

    map?.let {
        LaunchedEffect(uiState.orderDetails, loading) {
            launch(Dispatchers.Main) {
                updateRoute()
            }
        }
    }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            vm.getOrderHistory(orderId)
        }
    }

    map?.let { mapInstance ->
        HistoryDetailsScreen(
            uiState = uiState,
            loading = loading,
            map = mapInstance,
            onIntent = { intent ->
                when (intent) {
                    HistoryDetailsIntent.NavigateBack -> onNavigateBack()
                    HistoryDetailsIntent.OnMapReady -> updateRoute()
                }
            }
        )
    }

    if (showErrorDialog) {
        BaseDialog(
            title = stringResource(R.string.error),
            description = currentErrorMessageId?.let { stringResource(it) },
            actionText = stringResource(R.string.ok),
            onAction = { vm.dismissErrorDialog() },
            onDismiss = { vm.dismissErrorDialog() }
        )
    }

    if (loading) {
        LoadingDialog()
    }
}