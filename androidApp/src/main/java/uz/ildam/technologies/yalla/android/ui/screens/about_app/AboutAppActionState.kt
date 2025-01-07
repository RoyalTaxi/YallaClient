package uz.ildam.technologies.yalla.android.ui.screens.about_app

sealed interface AboutAppActionState {
    data object Loading : AboutAppActionState
    data object Error : AboutAppActionState
    data object Success : AboutAppActionState
}