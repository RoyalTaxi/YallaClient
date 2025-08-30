package uz.yalla.client.feature.promocode.presentation.add_promocode.model

import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.AddPromocodeIntent
import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.AddPromocodeSideEffect
import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.AddPromocodeState
import uz.yalla.client.feature.promocode.presentation.add_promocode.intent.PromocodeButtonState

fun AddPromocodeViewModel.onIntent(intent: AddPromocodeIntent) = intent {
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