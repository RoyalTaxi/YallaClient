package uz.yalla.client.feature.order.presentation.search.view

import androidx.compose.ui.unit.Dp
import uz.yalla.client.feature.order.domain.model.response.order.SearchCarModel

sealed interface SearchCarSheetIntent {
    data object OnCancelled : SearchCarSheetIntent
    data class SetSheetHeight(val height: Dp) : SearchCarSheetIntent
    data class OnFoundCars(val cars: SearchCarModel) : SearchCarSheetIntent
}