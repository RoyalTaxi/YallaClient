package uz.yalla.client.feature.history.history_details.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.dialog.BaseDialog
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.lifecycle.MakeBridge
import uz.yalla.client.core.common.map.ConcreteGoogleMap
import uz.yalla.client.core.common.map.ConcreteLibreMap
import uz.yalla.client.core.common.map.MapStrategy
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.MapType
import uz.yalla.client.core.domain.model.OrderStatus
import uz.yalla.client.feature.history.R
import uz.yalla.client.feature.history.history_details.intent.HistoryDetailsSideEffect
import uz.yalla.client.feature.history.history_details.model.HistoryDetailsViewModel
import uz.yalla.client.feature.history.history_details.model.onIntent
import uz.yalla.client.feature.history.history_details.navigation.FromHistoryDetails

@Composable
fun HistoryDetailsRoute(
    navigateTo: (FromHistoryDetails) -> Unit,
    orderId: Int,
    viewModel: HistoryDetailsViewModel = koinViewModel(
        parameters = { parametersOf(orderId) }
    )
) {
    val prefs = koinInject<AppPreferences>()
    val lifecycleOwner = LocalLifecycleOwner.current

    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val currentErrorMessageId by viewModel.currentErrorMessageId.collectAsStateWithLifecycle()

    val mapType by prefs.mapType.collectAsStateWithLifecycle(null)

    val originalMap: MapStrategy? = remember(mapType) {
        mapType?.let { type ->
            when (type) {
                MapType.Google -> ConcreteGoogleMap()
                MapType.Gis -> ConcreteGoogleMap()
                MapType.Libre -> ConcreteLibreMap()
            }
        }
    }

    val map: MapStrategy? = originalMap

    lifecycleOwner.MakeBridge(viewModel)

    viewModel.collectSideEffect { effect ->
        when(effect) {
            HistoryDetailsSideEffect.NavigateBack -> navigateTo(FromHistoryDetails.NavigateBack)
            HistoryDetailsSideEffect.UpdateRoute -> {
                map?.let { mapInstance ->
                    val routes = state.orderDetails?.taxi?.routes ?: return@let
                    if (routes.isEmpty()) return@let

                    val routePoints = routes.map { route ->
                        route.cords.let { MapPoint(it.lat, it.lng) }
                    }
                    if (routePoints.isEmpty()) return@let

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
        }
    }

    map?.let { mapInstance ->
        HistoryDetailsScreen(
            uiState = state,
            loading = loading,
            map = mapInstance,
            onIntent = viewModel::onIntent
        )
    }

    if (showErrorDialog) {
        BaseDialog(
            title = stringResource(R.string.error),
            description = currentErrorMessageId?.let { stringResource(it) },
            actionText = stringResource(R.string.ok),
            onAction = { viewModel.dismissErrorDialog() },
            onDismiss = { viewModel.dismissErrorDialog() }
        )
    }

    if (loading || !state.isMapReady) {
        LoadingDialog()
    }
}
