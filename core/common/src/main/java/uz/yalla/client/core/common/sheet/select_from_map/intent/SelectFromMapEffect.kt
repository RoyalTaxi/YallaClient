package uz.yalla.client.core.common.sheet.select_from_map.intent

import uz.yalla.client.core.domain.model.Location

sealed interface SelectFromMapEffect {
    data object NavigateBack : SelectFromMapEffect
    data class SelectLocation(val location: Location) : SelectFromMapEffect
}