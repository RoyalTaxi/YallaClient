package uz.yalla.client.feature.history.history_details.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.map.ConcreteGoogleMap
import uz.yalla.client.core.common.map.MapStrategy
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.MapType
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.history.history_details.model.HistoryDetailsActionState
import uz.yalla.client.feature.history.history_details.model.HistoryDetailsViewModel

@Composable
internal fun HistoryDetailsRoute(
    onNavigateBack: () -> Unit,
    orderId: Int,
    vm: HistoryDetailsViewModel = koinViewModel()
) {
    val prefs = koinInject<AppPreferences>()

    val uiState by vm.uiState.collectAsState()
    var loading by remember { mutableStateOf(true) }

    val mapType by prefs.mapType.collectAsState(initial = MapType.Google)

    val map: MapStrategy = remember(mapType) {
        when (mapType) {
            MapType.Google -> ConcreteGoogleMap()
            MapType.Gis -> ConcreteGoogleMap()
        }
    }

    fun updateRoute() {
        val routes = uiState.orderDetails?.taxi?.routes ?: return
        if (routes.isEmpty()) return

        val routePoints = routes.map { route ->
            route.cords.let { MapPoint(it.lat, it.lng) }
        }
        if (routePoints.isEmpty()) return

        if (routePoints.size == 1) {
            val singlePoint = routePoints.first()
            map.updateRoute(listOf(singlePoint))
            map.updateOrderStatus(OrderStatus.Appointed)
            map.move(singlePoint)
        } else {
            map.updateRoute(routePoints)
            map.moveToFitBounds(routePoints)
        }
        map.updateLocations(routePoints)
    }

    LaunchedEffect(uiState.orderDetails, loading) {
        launch(Dispatchers.Main) {
            updateRoute()
        }
    }

    LaunchedEffect(Unit) {
        // fetch history
        launch(Dispatchers.IO) {
            vm.getOrderHistory(orderId)
        }
        // observe actionState for loading & success
        launch(Dispatchers.Main) {
            vm.actionState.collectLatest { action ->
                loading = when (action) {
                    is HistoryDetailsActionState.Loading -> true
                    is HistoryDetailsActionState.DetailsSuccess -> {
                        vm.getMapPoints()
                        false
                    }

                    else -> false
                }
            }
        }
    }

    HistoryDetailsScreen(
        uiState = uiState,
        loading = loading,
        map = map,
        onIntent = { intent ->
            when (intent) {
                HistoryDetailsIntent.NavigateBack -> onNavigateBack()
                HistoryDetailsIntent.OnMapReady -> updateRoute()
            }
        }
    )

    if (loading) LoadingDialog()
}