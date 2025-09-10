package uz.yalla.client.feature.order.presentation.main.view

import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Location

sealed interface MainSheetAction {
    data class SetDestination(val destinations: List<Destination>) : MainSheetAction
    data class SetLocation(val location: Location) : MainSheetAction
    data class SetBonusInfoVisibility(val visible: Boolean) : MainSheetAction
}