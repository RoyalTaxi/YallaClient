package uz.yalla.client.core.common.sheet.select_from_map.intent

import androidx.compose.ui.unit.Dp

sealed interface SelectFromMapIntent {
    data object NavigateBack : SelectFromMapIntent
    data object SelectLocation : SelectFromMapIntent
    data object AnimateToMyLocation : SelectFromMapIntent
    data class SetSheetHeight(val height: Dp) : SelectFromMapIntent
    data class SetViewValue(val value: SelectFromMapViewValue) : SelectFromMapIntent
}
