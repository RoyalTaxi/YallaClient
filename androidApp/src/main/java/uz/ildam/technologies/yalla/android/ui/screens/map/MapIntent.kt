package uz.ildam.technologies.yalla.android.ui.screens.map

import uz.ildam.technologies.yalla.feature.order.domain.model.tarrif.GetTariffsModel

sealed interface MapIntent {
    data object MoveToMyLocation : MapIntent
    data object OpenDestinationLocationSheet : MapIntent
    data object OpenDrawer : MapIntent
    data class SelectTariff(
        val tariff: GetTariffsModel.Tariff,
        val wasSelected: Boolean
    ) : MapIntent
}