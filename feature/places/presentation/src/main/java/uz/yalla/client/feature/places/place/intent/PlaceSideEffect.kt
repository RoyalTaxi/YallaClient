package uz.yalla.client.feature.places.place.intent

sealed interface PlaceSideEffect {
    data object NavigateBack: PlaceSideEffect
    data object ConfirmCancellation: PlaceSideEffect
}