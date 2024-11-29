package uz.ildam.technologies.yalla.android.ui.screens.map

import uz.ildam.technologies.yalla.feature.order.domain.model.tarrif.GetTariffsModel

sealed interface MapIntent {
    data object MoveToMyLocation : MapIntent
    data object MoveToMyRoute : MapIntent
    data object SearchStartLocationSheet : MapIntent
    data object SearchEndLocationSheet : MapIntent
    data object OpenDrawer : MapIntent
    data object OpenOptions : MapIntent
    data object DiscardOrder : MapIntent
    data class SelectTariff(
        val tariff: GetTariffsModel.Tariff,
        val wasSelected: Boolean
    ) : MapIntent
}