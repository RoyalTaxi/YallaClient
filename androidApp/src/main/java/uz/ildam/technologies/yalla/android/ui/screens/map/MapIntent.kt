package uz.ildam.technologies.yalla.android.ui.screens.map

import uz.ildam.technologies.yalla.feature.order.domain.model.tarrif.GetTariffsModel

sealed interface MapIntent {
    data object MoveToMyLocation : MapIntent
    data object OpenCurrentLocationSearchSheet : MapIntent
    data object CloseCurrentLocationSearchSheet : MapIntent
    data object OpenDestinationLocationSearchSheet : MapIntent
    data object CloseDestinationLocationSearchSheet : MapIntent
    data class SelectTariff(
        val tariff: GetTariffsModel.Tariff,
        val wasSelected: Boolean
    ) : MapIntent
}