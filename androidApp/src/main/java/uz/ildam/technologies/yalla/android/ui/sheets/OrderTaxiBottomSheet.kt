package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.components.item.TariffItem
import uz.ildam.technologies.yalla.feature.order.domain.model.tarrif.GetTariffsModel

@Composable
fun OrderTaxiBottomSheet(
    tariffs: GetTariffsModel
) {
    Card(
        colors = CardDefaults.cardColors(YallaTheme.color.gray2),
        modifier = Modifier.navigationBarsPadding()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(YallaTheme.color.white),
                shape = RoundedCornerShape(30.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 20.dp)
                ) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(tariffs.tariff) { tariff ->
                            TariffItem(
                                tariff = tariff.name,
                                tariffImageUrl = tariff.photo,
                                startingCost = tariff.cost,
                                fixedCost = tariff.fixedPrice,
                                fixedState = tariff.fixedType,
                                selectedState = false,
                                onSelect = {}
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class SheetValue { Collapsed, PartiallyExpanded, Expanded }