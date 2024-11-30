package uz.ildam.technologies.yalla.android.ui.screens.details

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
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailsRoute(
    onNavigateBack: () -> Unit,
    orderId: Int,
    vm: DetailsViewModel = koinViewModel()
) {
    val uiState by vm.uiState.collectAsState()
    var loading by remember { mutableStateOf(true) }
    val cameraPositionState = remember { CameraPositionState() }

    LaunchedEffect(uiState.orderDetails) {
        if (uiState.orderDetails?.taxi?.routes?.size == 1) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(
                        LatLng(
                            uiState.orderDetails?.taxi?.routes?.first()?.cords?.lat ?: 0.0,
                            uiState.orderDetails?.taxi?.routes?.first()?.cords?.lng ?: 0.0
                        ), 15f, 0f, 0f
                    )
                )
            )
        } else if ((uiState.orderDetails?.taxi?.routes?.size ?: 0) > 1) {
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
                    is DetailsActionState.Loading -> true
                    is DetailsActionState.DetailsSuccess -> {
                        vm.getMapPoints()
                        false
                    }

                    else -> false
                }
            }
        }
    }

    DetailsScreen(
        uiState = uiState,
        loading = loading,
        cameraPositionState = cameraPositionState,
        onIntent = { intent ->
            when (intent) {
                DetailsIntent.NavigateBack -> onNavigateBack()
            }
        }
    )
}