package uz.yalla.client.core.common.sheet.select_from_map.intent

import androidx.compose.ui.unit.Dp
import uz.yalla.client.core.domain.model.Location

data class SelectFromMapState(
    val location: Location? = null,
    val isWorking: Boolean = false,
    val viewValue: SelectFromMapViewValue,
    val sheetHeight: Dp = Dp.Unspecified
)