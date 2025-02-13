package uz.ildam.technologies.yalla.android.ui.sheets

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.ui.components.button.SelectCurrentLocationButton
import uz.ildam.technologies.yalla.android.ui.components.button.SelectDestinationButton
import uz.ildam.technologies.yalla.android.ui.components.item.TariffItem
import uz.ildam.technologies.yalla.android.ui.components.item.TariffItemShimmer
import uz.ildam.technologies.yalla.android.ui.screens.map.MapUIState
import uz.ildam.technologies.yalla.android.utils.AnimatedScroll
import uz.ildam.technologies.yalla.android.utils.ScrollBehavior
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.feature.core.design.theme.YallaTheme

@Composable
fun OrderTaxiBottomSheet(
    isLoading: Boolean,
    uiState: MapUIState,
    listState: LazyListState,
    onSelectTariff: (GetTariffsModel.Tariff, Boolean) -> Unit,
    onCurrentLocationClick: () -> Unit,
    onDestinationClick: () -> Unit,
    onAddNewDestinationClick: () -> Unit,
    onAppear: (Dp) -> Unit
) {
    val density = LocalDensity.current
    LaunchedEffect(isLoading, uiState.selectedTariff, uiState.tariffs) {
        launch {
            if (!isLoading && uiState.tariffs?.tariff?.isNotEmpty() == true) {
                uiState.selectedTariff?.let { selected ->
                    centerTariff(
                        listState = listState,
                        tariffs = uiState.tariffs.tariff,
                        targetTariff = selected,
                        scrollBehavior = AnimatedScroll
                    )
                }
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(
                color = YallaTheme.color.gray2,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .onSizeChanged { size ->
                with(density) { onAppear(size.height.toDp()) }
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
                onClick = onCurrentLocationClick,
                text = if (isLoading.not() && uiState.selectedLocation?.name != null) uiState.selectedLocation.name
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
                destinations = uiState.destinations,
                modifier = Modifier.padding(horizontal = 20.dp),
                onClick = onDestinationClick,
                onAddNewLocation = onAddNewDestinationClick
            )

            val snappingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

            LazyRow(
                state = listState,
                flingBehavior = snappingBehavior,
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (uiState.tariffs?.tariff?.isNotEmpty() == true && isLoading.not()) {
                    items(uiState.tariffs.tariff) { tariff ->
                        TariffItem(
                            tariff = tariff,
                            isDestinationsEmpty = uiState.destinations.isEmpty(),
                            selectedState = uiState.selectedTariff?.id == tariff.id,
                            onSelect = { wasSelected -> onSelectTariff(tariff, wasSelected) }
                        )
                    }
                } else {
                    items(3) { TariffItemShimmer() }
                }
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