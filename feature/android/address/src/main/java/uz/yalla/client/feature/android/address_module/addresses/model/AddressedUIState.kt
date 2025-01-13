package uz.yalla.client.feature.android.address_module.addresses.model

import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressModel

internal data class AddressesUIState(
    val addresses: List<AddressModel>? = null
)