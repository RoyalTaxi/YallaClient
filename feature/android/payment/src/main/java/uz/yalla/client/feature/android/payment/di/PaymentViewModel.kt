package uz.yalla.client.feature.android.payment.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.payment.add_card.model.AddCardViewModel
import uz.yalla.client.feature.android.payment.card_list.model.CardListViewModel
import uz.yalla.client.feature.android.payment.card_verification.model.CardVerificationViewModel

object PaymentViewModel {
    val module = module {
        viewModelOf(::AddCardViewModel)
        viewModelOf(::CardListViewModel)
        viewModelOf(::CardVerificationViewModel)
    }
}