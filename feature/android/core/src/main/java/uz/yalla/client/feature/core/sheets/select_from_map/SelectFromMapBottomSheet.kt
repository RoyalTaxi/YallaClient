package uz.yalla.client.feature.core.sheets.select_from_map

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.android.awaitFrame
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.core.data.enums.MapType
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.ildam.technologies.yalla.core.domain.model.MapPoint
import uz.yalla.client.feature.core.R
import uz.yalla.client.feature.core.components.buttons.MapButton
import uz.yalla.client.feature.core.components.buttons.SelectCurrentLocationButton
import uz.yalla.client.feature.core.components.buttons.YButton
import uz.yalla.client.feature.core.components.marker.YallaMarker
import uz.yalla.client.feature.core.design.theme.YallaTheme
import uz.yalla.client.feature.core.map.ConcreteGisMap
import uz.yalla.client.feature.core.map.ConcreteGoogleMap

@Composable
fun SelectFromMapBottomSheet(
    startingPoint: MapPoint?,
    isForDestination: Boolean,
    isForNewDestination: Boolean,
    onSelectLocation: (String, Double, Double, Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: SelectFromMapBottomSheetViewModel = koinViewModel()
) {
    val density = LocalDensity.current
    val uiState by viewModel.uiState.collectAsState()
    var mapBottomPadding by remember { mutableStateOf(0.dp) }

    val map by remember(AppPreferences.mapType) {
        mutableStateOf(
            when (AppPreferences.mapType) {
                MapType.Google -> ConcreteGoogleMap()
                MapType.Gis -> ConcreteGisMap()
            }
        )
    }

    BackHandler(onBack = onDismissRequest)

    LaunchedEffect(map.isMarkerMoving.value) {
        if (map.isMarkerMoving.value) {
            viewModel.changeStateToNotFound()
        } else {
            viewModel.getAddressDetails(map.mapPoint.value)
        }
    }

    LaunchedEffect(mapBottomPadding) {
        if (mapBottomPadding > 0.dp) {
            awaitFrame()
            when {
                startingPoint == null -> map.moveToMyLocation()
                map.mapPoint.value != MapPoint.Zero -> {
                    map.move(map.mapPoint.value)
                    viewModel.getAddressDetails(map.mapPoint.value)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YallaTheme.color.gray2)
    ) {
        map.Map(
            startingPoint = startingPoint,
            modifier = Modifier.matchParentSize(),
            contentPadding = PaddingValues(bottom = mapBottomPadding)
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(20.dp)
                .padding(bottom = mapBottomPadding)
        ) {
            MapButton(
                painter = painterResource(R.drawable.ic_location),
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = map::animateToMyLocation
            )

            YallaMarker(
                time = uiState.timeout,
                isLoading = map.isMarkerMoving.value,
                color = YallaTheme.color.black,
                selectedAddressName = uiState.name,
                modifier = Modifier
                    .matchParentSize()
                    .align(Alignment.Center)
            )

            MapButton(
                onClick = onDismissRequest,
                painter = painterResource(R.drawable.ic_arrow_back),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(YallaTheme.color.gray2)
                .onSizeChanged { mapBottomPadding = with(density) { it.height.toDp() } }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                    )
            ) {
                SelectCurrentLocationButton(
                    modifier = Modifier.padding(10.dp),
                    text = uiState.name ?: stringResource(R.string.loading),
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .border(
                                    shape = CircleShape,
                                    width = 1.dp,
                                    color = YallaTheme.color.gray
                                )
                        )
                    },
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = YallaTheme.color.white,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .navigationBarsPadding()
            ) {
                YButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    enabled = !map.isMarkerMoving.value && (isForDestination || (uiState.addressId != null && uiState.name != null)),
                    text = stringResource(R.string.choose),
                    onClick = {
                        uiState.latLng?.let { latLng ->
                            uiState.name?.let { name ->
                                onSelectLocation(name, latLng.lat, latLng.lng, isForDestination || isForNewDestination)
                                onDismissRequest()
                            }
                        }
                    }
                )
            }
        }
    }
}