package uz.yalla.client.feature.places.places.model

import uz.yalla.client.feature.places.places.intent.AddressesIntent
import uz.yalla.client.feature.places.places.intent.AddressesSideEffect

internal fun AddressesViewModel.onIntent(intent: AddressesIntent) = intent {
    when (intent) {
        is AddressesIntent.OnAddNewAddress -> {
            postSideEffect(AddressesSideEffect.AddAddress(typeName = intent.type.typeName))
        }

        is AddressesIntent.OnClickAddress -> {
            postSideEffect(
                AddressesSideEffect.NavigateAddress(
                    typeName = intent.type.typeName,
                    id = intent.id
                )
            )
        }

        AddressesIntent.OnNavigateBack -> postSideEffect(AddressesSideEffect.NavigateBack)
    }
}