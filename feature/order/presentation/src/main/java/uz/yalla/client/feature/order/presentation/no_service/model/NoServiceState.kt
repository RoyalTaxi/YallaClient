package uz.yalla.client.feature.order.presentation.no_service.model

import uz.yalla.client.core.common.sheet.select_from_map.SelectFromMapViewValue

data class NoServiceState(
    val setLocationSheetVisibility: Boolean? = false,
    val selectFromMapVisibility: SelectFromMapViewValue = SelectFromMapViewValue.INVISIBLE
)