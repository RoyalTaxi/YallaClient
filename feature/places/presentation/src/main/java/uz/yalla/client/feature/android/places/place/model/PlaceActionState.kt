package uz.yalla.client.feature.android.places.place.model

internal sealed interface PlaceActionState {
    data object Loading : PlaceActionState
    data class Error(val errorMessage: String) : PlaceActionState
    data object GetSuccess : PlaceActionState
    data object PutSuccess : PlaceActionState
    data object DeleteSuccess : PlaceActionState
}