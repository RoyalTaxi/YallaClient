package uz.yalla.client.feature.places.place.model

import uz.yalla.client.feature.places.place.intent.PlaceIntent
import uz.yalla.client.feature.places.place.intent.PlaceSideEffect

internal fun PlaceViewModel.onIntent(intent: PlaceIntent) = intent {
    when (intent) {
        is PlaceIntent.OnChangeApartment -> updateApartment(intent.value)
        is PlaceIntent.OnChangeComment -> updateComment(intent.value)
        is PlaceIntent.OnChangeEntrance -> updateEnter(intent.value)
        is PlaceIntent.OnChangeFloor -> updateFloor(intent.value)
        is PlaceIntent.OnChangeName -> updateName(intent.value)
        is PlaceIntent.OnDelete -> {
            setConfirmationVisibility(true)
            postSideEffect(PlaceSideEffect.ConfirmCancellation)
            setDeleteId(intent.id)
        }
        PlaceIntent.OnNavigateBack -> postSideEffect(PlaceSideEffect.NavigateBack)
        PlaceIntent.OnSave -> {
            if (id == null) {
                createOneAddress()
            } else {
                updateOneAddress(id)
            }
        }
        PlaceIntent.OpenSearchSheet -> setSearchVisibility(true)
    }
}