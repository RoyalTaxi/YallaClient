package uz.ildam.technologies.yalla.android.ui.screens.details

sealed interface DetailsActionState {
    data object DetailsSuccess : DetailsActionState
    data object RouteSuccess : DetailsActionState
    data object Error : DetailsActionState
    data object Loading : DetailsActionState
}