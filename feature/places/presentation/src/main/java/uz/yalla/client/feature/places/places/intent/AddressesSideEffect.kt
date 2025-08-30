package uz.yalla.client.feature.places.places.intent

sealed interface AddressesSideEffect {
    data object NavigateBack : AddressesSideEffect
    data class NavigateAddress(val typeName: String, val id: Int) : AddressesSideEffect
    data class AddAddress(val typeName: String) : AddressesSideEffect
}