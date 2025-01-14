package uz.yalla.client.feature.android.history.history_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.core.data.enums.MapType
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.core.domain.model.MapPoint
import uz.yalla.client.feature.core.map.ConcreteGisMap
import uz.yalla.client.feature.core.map.ConcreteGoogleMap
import uz.yalla.client.feature.core.map.MapStrategy

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
        MapType.Gis -> ConcreteGisMap()
    }

    LaunchedEffect(uiState.orderDetails) {
        if (uiState.orderDetails?.taxi?.routes?.size == 1)
            uiState.orderDetails?.taxi?.routes?.first()?.cords?.let {
                map.move(MapPoint(it.lat, it.lng))
            }
        else if ((uiState.orderDetails?.taxi?.routes?.size ?: 0) > 1) {
            val boundsBuilder = LatLngBounds.Builder()
            uiState.orderDetails?.taxi?.routes?.forEach {
                boundsBuilder.include(LatLng(it.cords.lat, it.cords.lng))
            }
            val bounds = boundsBuilder.build()

            cameraPositionState.animate(update = CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    LaunchedEffect(Unit) {
        launch { vm.getOrderHistory(orderId) }

        launch {
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
        cameraPositionState = cameraPositionState,
        onIntent = { intent ->
            when (intent) {
                HistoryDetailsIntent.NavigateBack -> onNavigateBack()
            }
        }
    )
}