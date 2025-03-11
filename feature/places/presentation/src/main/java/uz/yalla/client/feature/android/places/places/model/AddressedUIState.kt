package uz.yalla.client.feature.android.places.places.model

import uz.yalla.client.feature.order.domain.model.response.PlaceModel


internal data class AddressesUIState(
    val addresses: List<PlaceModel>? = null
)