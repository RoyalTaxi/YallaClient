package uz.yalla.client.feature.android.places.place.view

internal sealed interface PlaceIntent {
    data object OnNavigateBack : PlaceIntent
    data object OnSave : PlaceIntent
    data class OnDelete(val id: Int) : PlaceIntent
    data class OnChangeName(val value: String) : PlaceIntent
    data class OnChangeApartment(val value: String) : PlaceIntent
    data class OnChangeEntrance(val value: String) : PlaceIntent
    data class OnChangeFloor(val value: String) : PlaceIntent
    data class OnChangeComment(val value: String) : PlaceIntent
    data object OpenSearchSheet : PlaceIntent
}