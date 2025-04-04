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
import uz.yalla.client.core.common.dialog.LoadingDialog
//import uz.yalla.client.core.common.map.ConcreteGisMap
import uz.yalla.client.core.common.map.ConcreteGoogleMap
import uz.yalla.client.core.common.map.MapStrategy
import uz.yalla.client.core.data.enums.MapType
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.history.history_details.model.HistoryDetailsActionState
import uz.yalla.client.feature.history.history_details.model.HistoryDetailsViewModel

@Composable
internal fun HistoryDetailsRoute(
    onNavigateBack: () -> Unit,
    orderId: Int,
    vm: HistoryDetailsViewModel = koinViewModel()
) {
    val uiState by vm.uiState.collectAsState()
    var loading by remember { mutableStateOf(true) }

    val map: MapStrategy = when (AppPreferences.mapType) {
        MapType.Google -> ConcreteGoogleMap()
        MapType.Gis -> ConcreteGoogleMap()
    }

    fun updateRoute() {
        if (uiState.orderDetails?.taxi?.routes?.size == 1)
            uiState.orderDetails?.taxi?.routes?.first()?.cords?.let {
                map.updateRoute(listOf(MapPoint(it.lat, it.lng)))
                map.updateOrderStatus(OrderStatus.Appointed)
                map.move(MapPoint(it.lat, it.lng))
            }
        else if ((uiState.orderDetails?.taxi?.routes?.size ?: 0) > 1) {
            uiState.orderDetails?.taxi?.routes?.let {
                map.updateRoute(it.map { p -> MapPoint(p.cords.lat, p.cords.lng) })
                map.moveToFitBounds(it.map { p -> MapPoint(p.cords.lat, p.cords.lng) })
            }
        }
    }

    LaunchedEffect(uiState.orderDetails, loading) {
        launch(Dispatchers.Main) {
            updateRoute()
        }
    }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            vm.getOrderHistory(orderId)
        }

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