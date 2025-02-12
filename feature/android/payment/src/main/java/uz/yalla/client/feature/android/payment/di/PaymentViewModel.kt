package uz.yalla.client.feature.android.payment.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.payment.add_card.model.AddCardViewModel
import uz.yalla.client.feature.android.payment.add_employee.model.AddEmployeeViewModel
import uz.yalla.client.feature.android.payment.business_account.model.BusinessAccountViewModel
import uz.yalla.client.feature.android.payment.corporate_account.model.CorporateAccountViewModel
import uz.yalla.client.feature.android.payment.card_list.model.CardListViewModel
import uz.yalla.client.feature.android.payment.card_verification.model.CardVerificationViewModel
import uz.yalla.client.feature.android.payment.top_up_balance.model.TopUpViewModel

object PaymentViewModel {
    val module = module {
        viewModelOf(::AddCardViewModel)
        viewModelOf(::CardListViewModel)
        viewModelOf(::CardVerificationViewModel)
        viewModelOf(::CorporateAccountViewModel)
        viewModelOf(::BusinessAccountViewModel)
        viewModelOf(::TopUpViewModel)
        viewModelOf(::AddEmployeeViewModel)
    }
}