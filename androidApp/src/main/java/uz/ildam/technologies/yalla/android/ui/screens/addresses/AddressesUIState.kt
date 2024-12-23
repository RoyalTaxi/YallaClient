package uz.ildam.technologies.yalla.android.ui.screens.addresses

import uz.ildam.technologies.yalla.feature.addresses.domain.model.response.AddressModel

data class AddressesUIState(
    val addresses: List<AddressModel>? = null
)