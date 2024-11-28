package uz.ildam.technologies.yalla.android.ui.screens.details

import uz.ildam.technologies.yalla.core.domain.error.DataError
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistoryModel

sealed interface DetailsActionState {
    data object DetailsSuccess : DetailsActionState
    data object RouteSuccess : DetailsActionState
    data class Error(val error: DataError.Network) : DetailsActionState
    data object Loading : DetailsActionState
}