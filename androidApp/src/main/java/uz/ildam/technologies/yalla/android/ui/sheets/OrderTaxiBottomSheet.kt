package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.button.OptionsButton
import uz.ildam.technologies.yalla.android.ui.components.button.SelectCurrentLocationButton
import uz.ildam.technologies.yalla.android.ui.components.button.SelectDestinationButton
import uz.ildam.technologies.yalla.android.ui.components.button.YallaButton
import uz.ildam.technologies.yalla.android.ui.components.item.TariffItem
import uz.ildam.technologies.yalla.android.ui.components.item.TariffItemShimmer
import uz.ildam.technologies.yalla.android.ui.screens.map.MapUIState
import uz.ildam.technologies.yalla.feature.order.domain.model.tarrif.GetTariffsModel

@Composable
fun OrderTaxiBottomSheet(
    isLoading: Boolean,
    uiState: MapUIState,
    onSelectTariff: (GetTariffsModel.Tariff, Boolean) -> Unit,
    onDestinationClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(YallaTheme.color.white)
            .navigationBarsPadding()
            .background(YallaTheme.color.gray2)
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
                currentLocation = uiState.selectedLocation?.name,
                isLoading = isLoading,
                modifier = Modifier.padding(horizontal = 20.dp),
                onClick = {}
            )

            SelectDestinationButton(
                destinations = uiState.destinations,
                modifier = Modifier.padding(horizontal = 20.dp),
                onClick = onDestinationClick
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (uiState.tariffs?.tariff?.isNotEmpty() == true && isLoading.not()) items(uiState.tariffs.tariff) { tariff ->
                    TariffItem(
                        tariff = tariff.name,
                        tariffImageUrl = tariff.photo,
                        startingCost = tariff.cost,
                        fixedCost = tariff.fixedPrice,
                        fixedState = tariff.fixedType,
                        selectedState = uiState.selectedTariff == tariff,
                        onSelect = { wasSelected -> onSelectTariff(tariff, wasSelected) }
                    )
                }
                else items(3) { TariffItemShimmer() }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .background(
                    color = YallaTheme.color.white,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )

                .padding(20.dp)
        ) {
            OptionsButton(
                modifier = Modifier.fillMaxHeight(),
                painter = painterResource(R.drawable.img_money),
                onClick = {}
            )

            YallaButton(
                text = stringResource(R.string.lets_go),
                enabled = isLoading.not() && uiState.selectedTariff != null,
                contentPadding = PaddingValues(vertical = 20.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {

            }

            OptionsButton(
                modifier = Modifier.fillMaxHeight(),
                painter = painterResource(R.drawable.img_options),
                onClick = {}
            )
        }
    }
}