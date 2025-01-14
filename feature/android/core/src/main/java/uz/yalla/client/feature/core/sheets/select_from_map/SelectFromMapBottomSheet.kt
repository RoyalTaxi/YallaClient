package uz.yalla.client.feature.core.sheets.select_from_map

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import uz.ildam.technologies.yalla.core.data.enums.MapType
import uz.ildam.technologies.yalla.core.data.local.AppPreferences
import uz.yalla.client.feature.core.R
import uz.yalla.client.feature.core.components.buttons.MapButton
import uz.yalla.client.feature.core.components.buttons.SelectCurrentLocationButton
import uz.yalla.client.feature.core.components.buttons.YButton
import uz.yalla.client.feature.core.components.marker.YallaMarker
import uz.yalla.client.feature.core.design.theme.YallaTheme
import uz.yalla.client.feature.core.map.ConcreteGisMap
import uz.yalla.client.feature.core.map.ConcreteGoogleMap
import uz.yalla.client.feature.core.map.MapStrategy

@Composable
fun SelectFromMapBottomSheet(
    modifier: Modifier = Modifier,
    isForDestination: Boolean,
    onSelectLocation: (String, Double, Double, Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: SelectFromMapBottomSheetViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val map: MapStrategy = remember {
        when (AppPreferences.mapType) {
            MapType.Google -> ConcreteGoogleMap()
            MapType.Gis -> ConcreteGisMap()
        }
    }

    BackHandler(onBack = onDismissRequest)

    LaunchedEffect(map.isMarkerMoving.value) {
        if (map.isMarkerMoving.value) viewModel.changeStateToNotFound()
        else viewModel.getAddressDetails(map.mapPoint.value)
    }

    LaunchedEffect(Unit) { map.moveToMyLocation() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(YallaTheme.color.gray2)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            map.Map(modifier = Modifier.matchParentSize())

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(20.dp)
            ) {
                YallaMarker(
                    time = uiState.timeout,
                    isLoading = map.isMarkerMoving.value,
                    color = YallaTheme.color.black,
                    selectedAddressName = uiState.name,
                    modifier = Modifier.align(Alignment.Center)
                )

                MapButton(
                    painter = painterResource(R.drawable.ic_location),
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = map::animateToMyLocation
                )

                MapButton(
                    onClick = onDismissRequest,
                    painter = painterResource(R.drawable.ic_arrow_back),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                )
            }
        }

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
                text = if (map.isMarkerMoving.value.not() && uiState.name != null) uiState.name!!
                else stringResource(R.string.loading),
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
        ) {
            YButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                enabled = !map.isMarkerMoving.value && if (isForDestination) true else uiState.addressId != null,
                text = stringResource(R.string.choose),
                onClick = {
                    if (uiState.latLng != null && uiState.name != null) {
                        onSelectLocation(
                            uiState.name!!,
                            uiState.latLng!!.lat,
                            uiState.latLng!!.lng,
                            isForDestination
                        )
                        onDismissRequest()
                    }
                }
            )
        }
    }
}