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
import uz.yalla.client.core.common.map.ConcreteLibreMap
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

    // Fixed: Use nullable mapType without default value
    val mapType by prefs.mapType.collectAsState(initial = null)

    // Fixed: Create map based on mapType with proper null handling
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
        // Only proceed if map is available
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

    // Only render screen when map is available
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

    // Show loading while waiting for map type preference or while fetching data
    if (loading || map == null) {
        LoadingDialog()
    }
}