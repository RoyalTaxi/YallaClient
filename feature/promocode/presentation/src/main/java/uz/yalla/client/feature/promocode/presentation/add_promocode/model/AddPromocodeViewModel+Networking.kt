package uz.yalla.client.feature.promocode.presentation.add_promocode.model

import androidx.lifecycle.viewModelScope
import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.AddPromocodeState
import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.PromocodeButtonState

fun AddPromocodeViewModel.activatePromocode() = intent {
    viewModelScope.launchWithLoading {
        val state = container.stateFlow.value
        if (state is AddPromocodeState.Idle) intent {
            activatePromocodeUseCase(state.promoCode).onSuccess { result ->
                reduce {
                    AddPromocodeState.Success(
                        data = result,
                        buttonState = PromocodeButtonState.OK
                    )
                }
            }.onFailure {
                reduce {
                    AddPromocodeState.Error(
                        message = it.message.orEmpty(),
                        buttonState = PromocodeButtonState.RETRY
                    )
                }
            }
        }
    }
}