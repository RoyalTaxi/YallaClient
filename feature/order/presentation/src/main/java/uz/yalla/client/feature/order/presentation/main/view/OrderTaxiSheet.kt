package uz.yalla.client.feature.order.presentation.main.view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.button.SelectCurrentLocationButton
import uz.yalla.client.core.common.button.SelectDestinationButton
import uz.yalla.client.core.common.state.AnimatedScroll
import uz.yalla.client.core.common.state.ScrollBehavior
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.TariffItem
import uz.yalla.client.feature.order.presentation.components.TariffItemShimmer
import uz.yalla.client.feature.order.presentation.main.model.MainSheetState
import uz.yalla.client.feature.order.presentation.main.view.MainBottomSheetIntent.OrderTaxiBottomSheetIntent
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel

@Composable
fun OrderTaxiBottomSheet(
    state: MainSheetState,
    modifier: Modifier = Modifier,
    onIntent: (OrderTaxiBottomSheetIntent) -> Unit
) {

    val density = LocalDensity.current
    val listState = LazyListState()

    LaunchedEffect(state.loading, state.selectedTariff, state.tariffs) {
        launch {
            if (state.loading && state.tariffs?.tariff?.isNotEmpty() == true) {
                state.selectedTariff?.let { selected ->
                    centerTariff(
                        listState = listState,
                        tariffs = state.tariffs.tariff,
                        targetTariff = selected,
                        scrollBehavior = AnimatedScroll
                    )
                }
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .background(
                color = YallaTheme.color.gray2,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .onSizeChanged { size ->
                with(density) {
                    size.height.toDp().let { height ->
                        if (height != state.sheetHeight)
                            onIntent(OrderTaxiBottomSheetIntent.SetSheetHeight(height))
                    }
                }
            }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(bottomEnd = 30.dp, bottomStart = 30.dp)
                )
                .padding(vertical = 20.dp)
        ) {

            SelectCurrentLocationButton(
                modifier = Modifier.padding(horizontal = 20.dp),
                onClick = { onIntent(OrderTaxiBottomSheetIntent.CurrentLocationClick) },
                text = if (state.loading && state.selectedLocation?.name != null) state.selectedLocation.name.orEmpty()
                else stringResource(R.string.loading),
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

            SelectDestinationButton(
                destinations = state.destinations,
                modifier = Modifier.padding(horizontal = 20.dp),
                onClick = { onIntent(OrderTaxiBottomSheetIntent.DestinationClick) },
                onAddNewLocation = { onIntent(OrderTaxiBottomSheetIntent.AddNewDestinationClick) }
            )

            val snappingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

            LazyRow(
                state = listState,
                flingBehavior = snappingBehavior,
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (state.tariffs?.tariff?.isNotEmpty() == true && state.loading.not()) {
                    items(state.tariffs.tariff) { tariff ->
                        TariffItem(
                            tariff = tariff,
                            isDestinationsEmpty = state.destinations.isEmpty(),
                            selectedState = state.selectedTariff?.id == tariff.id,
                            onSelect = { wasSelected ->
                                onIntent(
                                    OrderTaxiBottomSheetIntent.SelectTariff(tariff, wasSelected)
                                )
                            }
                        )
                    }
                } else items(3) { TariffItemShimmer() }
            }
        }
    }
}

private suspend fun centerTariff(
    listState: LazyListState,
    tariffs: List<GetTariffsModel.Tariff>,
    targetTariff: GetTariffsModel.Tariff,
    scrollBehavior: ScrollBehavior
) {
    val targetIndex = tariffs.indexOf(targetTariff)
    val visibleItem = listState.layoutInfo.visibleItemsInfo
        .firstOrNull { it.index == targetIndex }

    if (targetIndex == -1) return

    if (visibleItem != null) {
        val viewportCenter = (listState.layoutInfo.viewportStartOffset +
                listState.layoutInfo.viewportEndOffset) / 2
        val childCenter = visibleItem.offset + (visibleItem.size / 2)
        val distance = (childCenter - viewportCenter).toFloat()

        scrollBehavior.scrollBy(listState, distance)
    } else {
        scrollBehavior.scrollToItem(listState, targetIndex)
    }
}