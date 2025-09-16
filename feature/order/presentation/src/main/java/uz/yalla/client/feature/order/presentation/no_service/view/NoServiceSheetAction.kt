package uz.yalla.client.feature.order.presentation.no_service.view

import uz.yalla.client.core.domain.model.Location

sealed interface NoServiceSheetAction {
    data class SetLocation(val location: Location) : NoServiceSheetAction
}