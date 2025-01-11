package uz.ildam.technologies.yalla.android.ui.screens.details

sealed interface DetailsIntent {
    data object NavigateBack : DetailsIntent
}