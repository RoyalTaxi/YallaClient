package uz.yalla.client.feature.promocode.presentation.add_promocode.model

import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.feature.promocode.domain.usecase.ActivatePromocodeUseCase
import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.AddPromocodeIntent
import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.AddPromocodeSideEffect
import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.AddPromocodeState
import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.PromocodeButtonState

class AddPromocodeViewModel(
    private val activatePromocodeUseCase: ActivatePromocodeUseCase
) : BaseViewModel(), ContainerHost<AddPromocodeState, AddPromocodeSideEffect> {
    override val container: Container<AddPromocodeState, AddPromocodeSideEffect> =
        container(AddPromocodeState.INITIAL)

    fun activatePromocode() = viewModelScope.launchWithLoading {
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
                        message = it.message.toString(),
                        buttonState = PromocodeButtonState.RETRY
                    )
                }
            }
        }
    }

    fun onIntent(intent: AddPromocodeIntent) = intent {
        when (intent) {
            AddPromocodeIntent.ActivatePromocode -> activatePromocode()
            AddPromocodeIntent.NavigateBack -> postSideEffect(AddPromocodeSideEffect.NavigateBack)
            AddPromocodeIntent.RetryAgain -> reduce { AddPromocodeState.INITIAL }
            is AddPromocodeIntent.UpdatePromoCode -> reduce {
                AddPromocodeState.Idle(
                    intent.promoCode,
                    if (intent.promoCode.isEmpty()) PromocodeButtonState.ADD_DISABLED else PromocodeButtonState.ADD_ENABLED
                )
            }
        }
    }
}