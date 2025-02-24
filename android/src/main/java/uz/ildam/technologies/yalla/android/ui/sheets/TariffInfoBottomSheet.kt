package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.ui.components.button.ProvideDescriptionButton
import uz.ildam.technologies.yalla.android.ui.components.item.OptionsItem
import uz.ildam.technologies.yalla.android.ui.screens.map.MapUIState
import uz.ildam.technologies.yalla.feature.order.domain.model.response.tarrif.GetTariffsModel
import uz.yalla.client.feature.core.design.theme.YallaTheme

private typealias Options = List<GetTariffsModel.Tariff.Service>
private typealias Tariff = GetTariffsModel.Tariff

sealed interface TariffInfoAction {
    data object OnClickComment : TariffInfoAction
}

@Composable
fun TariffInfoBottomSheet(
    uiState: MapUIState,
    onOptionsChange: (Options) -> Unit,
    clearOptions: () -> Unit,
    onAction: (TariffInfoAction) -> Unit
) {
    uiState.selectedTariff?.let { tariff ->
        LazyColumn(
            modifier = Modifier.padding(bottom = uiState.footerHeight)
        ) {

            item {
                TariffInfoSection(
                    tariff = tariff,
                    isDestinationsEmpty = uiState.destinations.isEmpty()
                )
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }

            item {
                InfoProvidersSection(
                    info = uiState.comment,
                    onClick = { onAction(TariffInfoAction.OnClickComment) },
                    isOptionsValid = uiState.isTariffValidWithOptions ?: true,
                    clearOptions = clearOptions
                )
            }

            item {
                Column(modifier = Modifier.clip(RoundedCornerShape(30.dp))) {
                    uiState.options.forEach { service ->
                        OptionsItem(
                    options = service,
                            isSelected = uiState.selectedOptions.contains(service),
                    selectedOptions = uiState.selectedOptions,
                    onOptionsChange = onOptionsChange,
                    isOptionsValid = uiState.isTariffValidWithOptions ?: true
                )
            }
        }
    }
}

@Composable
private fun TariffSectionBackground(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
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
    TariffSectionBackground {
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
                contentScale = androidx.compose.ui.layout.ContentScale.FillWidth,
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
                    isDestinationsEmpty -> stringResource(R.string.starting_cost, tariff.cost)
                    tariff.fixedType -> stringResource(R.string.fixed_cost, tariff.fixedPrice)
                    else -> stringResource(R.string.fixed_cost, "~${tariff.fixedPrice}")
                }
            )
        }
    }
}

@Composable
private fun InfoProvidersSection(
    info: String,
    isOptionsValid: Boolean,
    clearOptions: () -> Unit,
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

    if (!isOptionsValid) {
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
}

@Composable
private fun OptionsSection(
    options: Options,
    selectedOptions: Options,
    isOptionsValid: Boolean,
    onOptionsChange: (Options) -> Unit
) {
    val newSelectedOptions = remember { mutableStateListOf(*selectedOptions.toTypedArray()) }

    LaunchedEffect(selectedOptions, isOptionsValid) {
        if (isOptionsValid) {
            val validOptions = selectedOptions.filter { selected ->
                options.any { option ->
                    option.name == selected.name && option.cost == selected.cost
                }
            }

            if (newSelectedOptions != validOptions) {
                newSelectedOptions.clear()
                newSelectedOptions.addAll(validOptions)
            }
        }
    }

    if (options.isNotEmpty()) TariffSectionBackground {
        LazyColumn {
            itemsIndexed(options) { index, option ->
                OptionsItem(
                    option = option,
                    isSelected = newSelectedOptions.any { it.name == option.name && it.cost == option.cost },
                    onChecked = { isSelected ->
                        newSelectedOptions.toggle(option, isSelected)
                        onOptionsChange(newSelectedOptions)
                    }
                )
            }
        }
    }
}


private fun MutableList<GetTariffsModel.Tariff.Service>.toggle(
    item: GetTariffsModel.Tariff.Service,
    shouldAdd: Boolean
) {
    if (shouldAdd) {
        if (none { it.name == item.name && it.cost == item.cost }) add(item)
    } else {
        removeIf { it.name == item.name && it.cost == item.cost }
    }
}

private fun <T> MutableList<T>.removeIf(predicate: (T) -> Boolean) {
    val iterator = iterator()
    while (iterator.hasNext()) {
        if (predicate(iterator.next())) {
            iterator.remove()
        }
    }
}