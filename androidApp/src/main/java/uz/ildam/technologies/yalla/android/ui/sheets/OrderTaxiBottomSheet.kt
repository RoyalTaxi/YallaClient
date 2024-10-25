package uz.ildam.technologies.yalla.android.ui.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import uz.ildam.technologies.yalla.android.ui.components.button.SelectLocationButton
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme

@Composable
fun OrderTaxiBottomSheet(
    locationFrom: String,
    locationTo: String,
    onLocationFromClick: () -> Unit,
    onLocationToClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(YallaTheme.color.gray2),
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(YallaTheme.color.white),
                shape = RoundedCornerShape(30.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SelectLocationButton(
                        location = locationFrom,
                        onClick = onLocationFromClick
                    )

                    SelectLocationButton(
                        location = locationTo,
                        onClick = onLocationToClick
                    )

                    LazyColumn {

                    }
                }
            }
        }
    }
}

enum class SheetValue { Collapsed, PartiallyExpanded, Expanded }