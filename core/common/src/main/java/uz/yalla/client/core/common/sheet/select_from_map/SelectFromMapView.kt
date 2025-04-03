package uz.yalla.client.core.common.sheet.select_from_map

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.button.MapButton
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.button.SelectCurrentLocationButton
import uz.yalla.client.core.common.dialog.LoadingDialog
import uz.yalla.client.core.common.map.ConcreteGoogleMap
import uz.yalla.client.core.common.map.MapStrategy
import uz.yalla.client.core.common.marker.YallaMarker
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.data.enums.MapType
import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.domain.model.SelectedLocation
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun SelectFromMapView(
    startingPoint: MapPoint?,
    viewValue: SelectFromMapViewValue,
    onSelectLocation: (SelectedLocation) -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: SelectFromMapViewModel = koinViewModel()
) {
    val density = LocalDensity.current
    val uiState by viewModel.uiState.collectAsState()
    var loading by remember { mutableStateOf(true) }
    var mapBottomPadding by remember { mutableStateOf(0.dp) }
    var isMarkerMoving by remember { mutableStateOf(false) }

    val map = rememberMapImplementation()

    val isLocationSelectionComplete by remember(
        uiState.selectedLocation,
        isMarkerMoving,
        viewValue
    ) {
        derivedStateOf {
            !isMarkerMoving && (
                    viewValue == SelectFromMapViewValue.FOR_DEST ||
                            (uiState.selectedLocation?.addressId != null &&
                                    uiState.selectedLocation?.name != null)
                    )
        }
    }

    BackHandler(onBack = onDismissRequest)

    LaunchedEffect(Unit) {
        launch(Dispatchers.Main) {
            map.isMarkerMoving.collectLatest {
                isMarkerMoving = it
            }
        }
    }

    LaunchedEffect(isMarkerMoving) {
        if (isMarkerMoving) {
            viewModel.changeStateToNotFound()
        } else {
            viewModel.getAddressName(map.mapPoint.value)
        }
    }

    LaunchedEffect(mapBottomPadding) {
        if (mapBottomPadding > 0.dp) {
            launch(Dispatchers.Main) {
                awaitFrame()
                initializeMapPosition(map, startingPoint, viewModel)
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
            .background(YallaTheme.color.gray2)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(Unit) {}
        )

        MapContent(
            map = map,
            mapBottomPadding = mapBottomPadding,
            startingPoint = startingPoint,
            onMapReady = {
                loading = false
            }
        )

        MapControlsLayer(
            map = map,
            mapBottomPadding = mapBottomPadding,
            isMarkerMoving = isMarkerMoving,
            selectedLocationName = uiState.selectedLocation?.name,
            onBackClick = onDismissRequest,
            boxPadding = boxPadding
        )

        BottomPanel(
            selectedLocationName = uiState.selectedLocation?.name,
            isButtonEnabled = isLocationSelectionComplete,
            onSizeChanged = { height -> mapBottomPadding = with(density) { height.toDp() } },
            onConfirmClick = {
                uiState.selectedLocation?.let {
                    onSelectLocation(it)
                    onDismissRequest()
                }
            },
            topCornerShape = topCornerShape,
            bottomCornerShape = bottomCornerShape,
            standardPadding = standardPadding
        )
    }

    if (loading) LoadingDialog()
}

@Composable
private fun rememberMapImplementation(): MapStrategy {
    return remember(AppPreferences.mapType) {
        when (AppPreferences.mapType) {
            MapType.Google -> ConcreteGoogleMap()
            MapType.Gis -> ConcreteGoogleMap()
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
        onMapReady = onMapReady
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
            color = YallaTheme.color.black,
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
            .background(YallaTheme.color.gray2)
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
            .background(
                color = YallaTheme.color.white,
                shape = bottomCornerShape
            )
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
            onClick = { }
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
            .background(
                color = YallaTheme.color.white,
                shape = topCornerShape
            )
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