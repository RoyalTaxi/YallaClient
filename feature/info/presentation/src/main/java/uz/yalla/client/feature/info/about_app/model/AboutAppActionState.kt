package uz.yalla.client.feature.info.about_app.model

internal sealed interface AboutAppActionState {
    data object Loading : AboutAppActionState
    data object Error : AboutAppActionState
    data object Success : AboutAppActionState
}