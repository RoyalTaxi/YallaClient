package uz.yalla.client.core.common.sheet.select_from_map.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.button.MapButton
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.common.button.SelectCurrentLocationButton
import uz.yalla.client.core.common.marker.YallaMarker
import uz.yalla.client.core.common.marker.YallaMarkerState
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapIntent
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapState
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapViewValue
import uz.yalla.client.core.presentation.design.theme.YallaTheme

@Composable
fun SelectFromMapView(
    state: SelectFromMapState,
    onIntent: (SelectFromMapIntent) -> Unit,
    map: @Composable (modifier: Modifier) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        map(Modifier.padding(bottom = 100.dp))

        YallaMarker(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 100.dp),
            state = if (state.location == null) YallaMarkerState.LOADING else YallaMarkerState.IDLE(
                title = state.location.name,
                timeout = null
            )
        )

        Overlay(
            state = state,
            onIntent = onIntent,
        )
    }
}

@Composable
private fun Overlay(
    state: SelectFromMapState,
    onIntent: (SelectFromMapIntent) -> Unit
) {
    Column {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    horizontal = 16.dp,
                    vertical = 24.dp
                )
        ) {
            MapButton(
                modifier = Modifier.align(Alignment.Start),
                painter = painterResource(R.drawable.ic_arrow_back),
                onClick = { onIntent(SelectFromMapIntent.NavigateBack) }
            )

            Spacer(modifier = Modifier.weight(1f))

            MapButton(
                modifier = Modifier.align(Alignment.End),
                painter = painterResource(R.drawable.ic_location),
                onClick = { onIntent(SelectFromMapIntent.AnimateToMyLocation) }
            )
        }

        Card(
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            colors = CardDefaults.cardColors(YallaTheme.color.surface)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(YallaTheme.color.background)
                ) {
                    SelectCurrentLocationButton(
                        text = state.location?.name ?: stringResource(R.string.loading),
                        modifier = Modifier.padding(10.dp),
                        onClick = {},
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = YallaTheme.color.primary,
                                        shape = CircleShape
                                    )
                            )
                        }
                    )
                }

                Card(
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(YallaTheme.color.background),
                ) {
                    PrimaryButton(
                        text = stringResource(R.string.choose),
                        onClick = { onIntent(SelectFromMapIntent.SelectLocation) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .navigationBarsPadding(),
                        enabled = when (state.viewValue) {
                            SelectFromMapViewValue.FOR_START -> state.location?.point != null && state.isWorking
                            else -> state.location?.point != null
                        }
                    )
                }
            }
        }
    }
}