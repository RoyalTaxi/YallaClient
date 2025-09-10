package uz.yalla.client.core.common.sheet.select_from_map

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.button.MapButton
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.button.SelectCurrentLocationButton
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.map.lite.ConcreteGoogleMap
import uz.yalla.client.core.common.map.lite.ConcreteLibreMap
import uz.yalla.client.core.common.map.lite.MapStrategy
import uz.yalla.client.core.common.marker.YallaMarker
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.Location
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.MapType
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun SelectFromMapView(
    startingPoint: MapPoint?,
    viewValue: SelectFromMapViewValue,
    onSelectLocation: (Location) -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: SelectFromMapViewModel = koinViewModel()
) {
    val density = LocalDensity.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var loading by remember { mutableStateOf(true) }
    var mapBottomPadding by remember { mutableStateOf(0.dp) }
    var isMarkerMoving by remember { mutableStateOf(false) }

    val map = rememberMapImplementation()

    val isLocationSelectionComplete by remember(
        uiState.location,
        isMarkerMoving,
        viewValue
    ) {
        derivedStateOf {
            !isMarkerMoving && (
                    viewValue == SelectFromMapViewValue.FOR_DEST ||
                            (uiState.location?.addressId != null &&
                                    uiState.location?.name != null)
                    )
        }
    }

    BackHandler(onBack = onDismissRequest)

    map?.let { mapInstance ->
        LaunchedEffect(Unit) {
            mapInstance.isMarkerMoving.collectLatest { pair ->
                isMarkerMoving = pair.first
            }
        }

        LaunchedEffect(isMarkerMoving) {
            if (isMarkerMoving) {
                viewModel.changeStateToNotFound()
            } else {
                viewModel.getAddressName(mapInstance.mapPoint.value)
            }
        }

        LaunchedEffect(mapBottomPadding) {
            if (mapBottomPadding > 0.dp) {
                launch(Dispatchers.Main) {
                    awaitFrame()
                    initializeMapPosition(mapInstance, startingPoint, viewModel)
                }
            }
        }
    }

    val topCornerShape = remember { RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp) }
    val bottomCornerShape = remember { RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp) }
    val standardPadding = remember { 10.dp }
    val boxPadding = remember { 20.dp }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YallaTheme.color.surface)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(Unit) { }
        )

        // Only render map if available
        map?.let { mapInstance ->
            MapContent(
                map = mapInstance,
                mapBottomPadding = mapBottomPadding,
                startingPoint = startingPoint,
                onMapReady = { loading = false }
            )

            MapControlsLayer(
                map = mapInstance,
                mapBottomPadding = mapBottomPadding,
                isMarkerMoving = isMarkerMoving,
                selectedLocationName = uiState.location?.name,
                onBackClick = onDismissRequest,
                boxPadding = boxPadding
            )
        }

        BottomPanel(
            selectedLocationName = uiState.location?.name,
            isButtonEnabled = isLocationSelectionComplete,
            onSizeChanged = { height -> mapBottomPadding = with(density) { height.toDp() } },
            onConfirmClick = {
                uiState.location?.let {
                    if (it.addressId != null) onSelectLocation(it)
                    onDismissRequest()
                }
            },
            topCornerShape = topCornerShape,
            bottomCornerShape = bottomCornerShape,
            standardPadding = standardPadding
        )
    }

    // Show loading while waiting for map type or map initialization
    if (loading || map == null) LoadingDialog()
}

@Composable
private fun rememberMapImplementation(): MapStrategy? {
    val prefs = koinInject<AppPreferences>()

    val mapType by prefs
        .mapType
        .collectAsStateWithLifecycle(null)

    return remember(mapType) {
        mapType?.let { type ->
            when (type) {
                MapType.Google -> ConcreteGoogleMap()
                MapType.Libre -> ConcreteLibreMap()
            }
        }
    }
}

private fun initializeMapPosition(
    map: MapStrategy,
    startingPoint: MapPoint?,
    viewModel: SelectFromMapViewModel
) {
    when {
        startingPoint == null -> map.moveToMyLocation()
        map.mapPoint.value != MapPoint.Zero -> {
            map.move(map.mapPoint.value)
            viewModel.getAddressName(map.mapPoint.value)
        }
    }
}

@Composable
private fun BoxScope.MapContent(
    map: MapStrategy,
    mapBottomPadding: Dp,
    startingPoint: MapPoint?,
    onMapReady: () -> Unit
) {
    map.Map(
        startingPoint = startingPoint,
        modifier = Modifier.matchParentSize(),
        enabled = true,
        contentPadding = PaddingValues(bottom = mapBottomPadding),
        onMapReady = onMapReady,
        isMyLocationEnabled = true,
    )
}

@Composable
private fun BoxScope.MapControlsLayer(
    map: MapStrategy,
    mapBottomPadding: Dp,
    isMarkerMoving: Boolean,
    selectedLocationName: String?,
    onBackClick: () -> Unit,
    boxPadding: Dp
) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .padding(boxPadding)
            .padding(bottom = mapBottomPadding)
    ) {
        MapButton(
            painter = painterResource(R.drawable.ic_location),
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = map::animateToMyLocation
        )

        val markerState = if (isMarkerMoving) {
            YallaMarkerState.LOADING
        } else {
            YallaMarkerState.IDLE(
                title = selectedLocationName,
                timeout = null
            )
        }

        YallaMarker(
            color = YallaTheme.color.primary,
            state = markerState,
            modifier = Modifier
                .matchParentSize()
                .align(Alignment.Center)
        )

        MapButton(
            onClick = onBackClick,
            painter = painterResource(R.drawable.ic_arrow_back),
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
        )
    }
}

@Composable
private fun BoxScope.BottomPanel(
    selectedLocationName: String?,
    isButtonEnabled: Boolean,
    onSizeChanged: (Int) -> Unit,
    onConfirmClick: () -> Unit,
    topCornerShape: RoundedCornerShape,
    bottomCornerShape: RoundedCornerShape,
    standardPadding: Dp
) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .clip(topCornerShape)
            .background(YallaTheme.color.surface)
            .onSizeChanged { onSizeChanged(it.height) }
    ) {
        LocationDisplaySection(
            selectedLocationName = selectedLocationName,
            bottomCornerShape = bottomCornerShape,
            standardPadding = standardPadding
        )

        Spacer(modifier = Modifier.height(10.dp))

        ActionButtonSection(
            isEnabled = isButtonEnabled,
            onClick = onConfirmClick,
            topCornerShape = topCornerShape,
            standardPadding = standardPadding
        )
    }
}

@Composable
private fun LocationDisplaySection(
    selectedLocationName: String?,
    bottomCornerShape: RoundedCornerShape,
    standardPadding: Dp
) {
    val circleShape = remember { CircleShape }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = YallaTheme.color.background, shape = bottomCornerShape)
    ) {
        SelectCurrentLocationButton(
            modifier = Modifier.padding(standardPadding),
            text = selectedLocationName ?: stringResource(R.string.loading),
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .border(
                            shape = circleShape,
                            width = 1.dp,
                            color = YallaTheme.color.gray
                        )
                )
            },
            onClick = { /* no-op */ }
        )
    }
}

@Composable
private fun ActionButtonSection(
    isEnabled: Boolean,
    onClick: () -> Unit,
    topCornerShape: RoundedCornerShape,
    standardPadding: Dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = YallaTheme.color.background, shape = topCornerShape)
            .navigationBarsPadding()
    ) {
        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(standardPadding),
            enabled = isEnabled,
            text = stringResource(R.string.choose),
            onClick = onClick
        )
    }
}