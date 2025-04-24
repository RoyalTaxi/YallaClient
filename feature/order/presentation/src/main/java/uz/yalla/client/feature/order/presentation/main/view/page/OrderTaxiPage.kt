package uz.yalla.client.feature.order.presentation.main.view.page

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.button.SelectCurrentLocationButton
import uz.yalla.client.core.common.button.SelectDestinationButton
import uz.yalla.client.core.data.mapper.or0
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.items.TariffItem
import uz.yalla.client.feature.order.presentation.components.items.TariffItemShimmer
import uz.yalla.client.feature.order.presentation.main.model.MainSheetState
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.OrderTaxiSheetIntent

@Composable
fun OrderTaxiPage(
    state: MainSheetState,
    modifier: Modifier = Modifier,
    onHeightChanged: (Dp) -> Unit,
    onIntent: (OrderTaxiSheetIntent) -> Unit
) {
    val density = LocalDensity.current

    val coroutineScope = rememberCoroutineScope()

    val selectedIndex by remember(state.selectedTariff, state.tariffs) {
        derivedStateOf {
            if (state.selectedTariff != null) {
                0
            } else {
                state.tariffs?.tariff
                    ?.indexOfFirst { it.id == state.selectedTariff?.id }
                    .takeIf { it.or0() >= 0 } ?: 0
            }
        }
    }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val snappingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val viewportWidthPx = remember { mutableIntStateOf(0) }

    fun centerItemInList(index: Int) {
        if (index < 0 || viewportWidthPx.intValue <= 0) return

        coroutineScope.launch {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            val itemInfo = visibleItemsInfo.firstOrNull { it.index == index }

            if (itemInfo != null) {
                val itemCenter = itemInfo.offset + (itemInfo.size / 2)
                val viewportCenter =
                    layoutInfo.viewportStartOffset + (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
                val scrollOffset = (itemCenter - viewportCenter)

                listState.animateScrollBy(scrollOffset.toFloat())
            } else {
                listState.animateScrollToItem(index)

                delay(100)

                val updatedLayoutInfo = listState.layoutInfo
                val updatedVisibleItemsInfo = updatedLayoutInfo.visibleItemsInfo
                val updatedItemInfo =
                    updatedVisibleItemsInfo.firstOrNull { it.index == index } ?: return@launch

                val itemCenter = updatedItemInfo.offset + (updatedItemInfo.size / 2)
                val viewportCenter = updatedLayoutInfo.viewportStartOffset +
                        (updatedLayoutInfo.viewportEndOffset - updatedLayoutInfo.viewportStartOffset) / 2
                val scrollOffset = (itemCenter - viewportCenter)

                listState.scrollBy(scrollOffset.toFloat())
            }
        }
    }

    LaunchedEffect(selectedIndex, state.tariffs, viewportWidthPx.intValue) {
        centerItemInList(selectedIndex)
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
                        if (height != state.sheetHeight) {
                            onHeightChanged(height)
                        }
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
                onClick = { onIntent(OrderTaxiSheetIntent.CurrentLocationClick) },
                text = if (state.loading.not() && state.selectedLocation?.name != null) {
                    state.selectedLocation.name.orEmpty()
                } else {
                    stringResource(R.string.loading)
                },
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
                }
            )

            SelectDestinationButton(
                destinations = state.destinations,
                modifier = Modifier.padding(horizontal = 20.dp),
                onClick = { onIntent(OrderTaxiSheetIntent.DestinationClick) },
                onAddNewLocation = { onIntent(OrderTaxiSheetIntent.AddNewDestinationClick) },
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

            LazyRow(
                state = listState,
                flingBehavior = snappingBehavior,
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.onSizeChanged { size ->
                    viewportWidthPx.intValue = size.width
                }
            ) {
                if (state.tariffs?.tariff?.isNotEmpty() == true) {
                    items(state.tariffs.tariff) { tariff ->
                        TariffItem(
                            tariff = tariff,
                            isDestinationsEmpty = state.destinations.isEmpty(),
                            selectedState = state.selectedTariff?.id == tariff.id,
                            onSelect = { wasSelected ->
                                coroutineScope.launch {
                                    onIntent(
                                        OrderTaxiSheetIntent.SelectTariff(
                                            tariff,
                                            wasSelected
                                        )
                                    )

                                    val targetIndex = state.tariffs.tariff.indexOf(tariff)
                                    if (targetIndex >= 0 && viewportWidthPx.intValue > 0) {
                                        centerItemInList(targetIndex)
                                    }
                                }
                            }
                        )
                    }
                } else items(3) { TariffItemShimmer() }
            }
        }
    }
}