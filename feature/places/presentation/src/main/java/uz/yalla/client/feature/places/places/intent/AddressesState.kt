package uz.yalla.client.feature.places.places.intent

import uz.yalla.client.feature.order.domain.model.response.PlaceModel


internal data class AddressesState(
    val addresses: List<PlaceModel>?
) {
    companion object {
        val INITIAL = AddressesState(
            addresses = null
        )
    }
}