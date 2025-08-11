package uz.yalla.client.feature.promocode.presentation.add_promocode.intent

import uz.yalla.client.feature.promocode.domain.model.PromocodeActivationModel

enum class PromocodeButtonState {
    ADD_ENABLED, ADD_DISABLED, OK, RETRY
}

sealed interface AddPromocodeState {
    data class Idle(
        val promoCode: String,
        val buttonState: PromocodeButtonState
    ) : AddPromocodeState

    data class Success(
        val data: PromocodeActivationModel,
        val buttonState: PromocodeButtonState
    ) : AddPromocodeState

    data class Error(
        val message: String,
        val buttonState: PromocodeButtonState
    ) : AddPromocodeState

    companion object {
        val INITIAL = Idle("", PromocodeButtonState.ADD_DISABLED)
    }
}