package uz.yalla.client.feature.order.presentation.main.view.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import uz.yalla.client.core.domain.model.ServiceModel
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.items.OptionsItem
import uz.yalla.client.feature.order.presentation.components.buttons.ProvideDescriptionButton
import uz.yalla.client.feature.order.presentation.main.model.MainSheetState
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.TariffInfoSheetIntent

private typealias Tariff = GetTariffsModel.Tariff

@Composable
fun TariffInfoPage(
    state: MainSheetState,
    isTariffValidWithOptions: Boolean,
    modifier: Modifier = Modifier,
    onIntent: (TariffInfoSheetIntent) -> Unit
) {
    val columnState = rememberLazyListState()
    val newSelectedOptions = remember(state.selectedOptions) {
        mutableStateListOf(*state.selectedOptions.toTypedArray())
    }

    val endOfListReached by remember {
        derivedStateOf {
            columnState.isScrolledToEnd()
        }
    }

    LaunchedEffect(endOfListReached) {
        onIntent(
            TariffInfoSheetIntent.ChangeShadowVisibility(
                visible = endOfListReached.not()
            )
        )
    }

    state.selectedTariff?.let { tariff ->
        LazyColumn(
            state = columnState,
            modifier = modifier.fillMaxSize()
        ) {
            item {
                TariffInfoSection(
                    tariff = tariff,
                    isDestinationsEmpty = state.destinations.isEmpty()
                )
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }

            item {
                InfoProvidersSection(
                    info = state.comment,
                    onClick = { onIntent(TariffInfoSheetIntent.ClickComment) }
                )
            }

            if (isTariffValidWithOptions.not()) {
                item { Spacer(modifier = Modifier.height(10.dp)) }

                item {
                    InvalidOptionsSection(
                        clearOptions = { onIntent(TariffInfoSheetIntent.ClearOptions) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }


            item {
                Column(
                    modifier = Modifier.clip(RoundedCornerShape(30.dp))
                ) {
                    state.options.forEach { service ->
                        OptionsItem(
                            option = service,
                            isSelected = newSelectedOptions.any {
                                it.name == service.name && it.cost == service.cost
                            },
                            onChecked = { isSelected ->
                                newSelectedOptions.toggle(service, isSelected)
                                onIntent(
                                    TariffInfoSheetIntent.OptionsChange(
                                        options = newSelectedOptions
                                    )
                                )
                            }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(state.footerHeight)) }
        }
    }
}

@Composable
private fun TariffSectionBackground(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(30.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(YallaTheme.color.white)
    ) {
        Column { content() }
    }
}

@Composable
private fun TariffInfoSection(
    tariff: Tariff,
    isDestinationsEmpty: Boolean
) {
    TariffSectionBackground(
        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .statusBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .height(4.dp)
                    .padding(top = 4.dp, bottom = 2.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(YallaTheme.color.gray2)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tariff.name,
                color = YallaTheme.color.black,
                style = YallaTheme.font.title,
            )

            if (tariff.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = tariff.description,
                    color = YallaTheme.color.black,
                    style = YallaTheme.font.label,
                )
            }

            AsyncImage(
                model = tariff.photo,
                contentDescription = null,
                placeholder = painterResource(R.drawable.img_default_car),
                error = painterResource(R.drawable.img_default_car),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(20.dp)
            )

            Text(
                style = YallaTheme.font.labelSemiBold,
                color = YallaTheme.color.black,
                modifier = Modifier.padding(vertical = 10.dp),
                text = when {
                    isDestinationsEmpty -> stringResource(
                        R.string.starting_cost,
                        tariff.cost
                    )

                    tariff.fixedType -> stringResource(
                        R.string.fixed_cost,
                        tariff.fixedPrice
                    )

                    else -> stringResource(R.string.fixed_cost, "~${tariff.fixedPrice}")
                }
            )
        }
    }
}

@Composable
private fun InfoProvidersSection(
    info: String,
    onClick: () -> Unit
) {

    TariffSectionBackground(
        modifier = Modifier.height(IntrinsicSize.Min)
    ) {
        ProvideDescriptionButton(
            modifier = Modifier.fillMaxHeight(),
            title = stringResource(R.string.comment_to_driver),
            description = info,
            onClick = onClick,
            trailingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = YallaTheme.color.gray
                )
            }
        )
    }
}

@Composable
fun InvalidOptionsSection(
    clearOptions: () -> Unit,
) {
    TariffSectionBackground(
        modifier = Modifier.height(IntrinsicSize.Min)
    ) {
        ProvideDescriptionButton(
            modifier = Modifier.fillMaxHeight(),
            title = stringResource(R.string.invalid_options),
            textColor = YallaTheme.color.red,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = YallaTheme.color.red
                )
            },
            onClick = clearOptions
        )
    }

}

private fun MutableList<ServiceModel>.toggle(
    item: ServiceModel,
    shouldAdd: Boolean
) {
    if (shouldAdd) {
        if (none { it.name == item.name && it.cost == item.cost }) add(item)
    } else {
        removeIf { it.name == item.name && it.cost == item.cost }
    }
}

private fun LazyListState.isScrolledToEnd(): Boolean {
    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return false
    return lastVisibleItem.index == layoutInfo.totalItemsCount - 1 &&
            lastVisibleItem.offset + lastVisibleItem.size <= layoutInfo.viewportEndOffset
}