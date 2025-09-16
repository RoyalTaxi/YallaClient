package uz.yalla.client.core.common.sheet.select_from_map.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.lifecycle.MakeBridge
import uz.yalla.client.core.common.map.lite.google.LiteGoogleMap
import uz.yalla.client.core.common.map.lite.libre.LiteLibreMap
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapEffect
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapIntent
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapViewValue
import uz.yalla.client.core.common.sheet.select_from_map.model.SelectFromMapViewModel
import uz.yalla.client.core.common.sheet.select_from_map.model.onIntent
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.MapType

@Composable
fun SelectFromMapScreen(
    startingPoint: MapPoint?,
    viewValue: SelectFromMapViewValue,
    onDismissRequest: () -> Unit,
    onSelectLocation: (Location) -> Unit,
    appPreferences: AppPreferences = koinInject(),
    viewModel: SelectFromMapViewModel = koinViewModel(parameters = { parametersOf(startingPoint) })
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val isMapReady by viewModel.liteMapViewModel.isMapReady.collectAsStateWithLifecycle(false)
    val mapType by appPreferences.mapType.collectAsStateWithLifecycle(null)
    val map = remember(mapType) {
        when (mapType) {
            MapType.Google -> LiteGoogleMap()
            MapType.Libre -> LiteLibreMap()
            null -> null
        }
    }

    lifecycleOwner.MakeBridge(viewModel)

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SelectFromMapEffect.NavigateBack -> onDismissRequest()
            is SelectFromMapEffect.SelectLocation -> onSelectLocation(effect.location)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onIntent(SelectFromMapIntent.SetViewValue(viewValue))
    }

    SelectFromMapView(
        state = state,
        onIntent = viewModel::onIntent
    ) {
        map?.View(viewModel.liteMapViewModel)
    }

    if (loading || isMapReady) {
        LoadingDialog()
    }
}