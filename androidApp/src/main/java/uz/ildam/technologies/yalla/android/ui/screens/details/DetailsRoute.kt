package uz.ildam.technologies.yalla.android.ui.screens.details

import androidx.compose.runtime.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistoryModel

@Composable
fun DetailsRoute(
    onNavigateBack: () -> Unit,
    orderId: Int,
    vm: DetailsViewModel = koinViewModel()
) {
    val uiState by vm.uiState.collectAsState()
    var loading by remember { mutableStateOf(true) }
    val cameraPositionState = remember { CameraPositionState() }

    LaunchedEffect(uiState.routes) {
        if (uiState.routes.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.Builder()
            uiState.routes.forEach { boundsBuilder.include(LatLng(it.lat, it.lng)) }
            val bounds = boundsBuilder.build()

            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(bounds, 100),
                durationMs = 1000
            )
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

    if (loading.not()) DetailsScreen(
        uiState = uiState,
        cameraPositionState = cameraPositionState,
        onIntent = { intent ->
            when (intent) {
                DetailsIntent.NavigateBack -> onNavigateBack()
            }
        }
    )
}