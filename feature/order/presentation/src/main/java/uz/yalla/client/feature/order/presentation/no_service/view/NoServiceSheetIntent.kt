package uz.yalla.client.feature.order.presentation.no_service.view

import uz.yalla.client.core.domain.model.SelectedLocation

sealed interface NoServiceSheetIntent {
    data class SetSelectedLocation(val location: SelectedLocation) : NoServiceSheetIntent
}