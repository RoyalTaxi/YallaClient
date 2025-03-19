package uz.yalla.client.feature.payment.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.payment.add_card.model.AddCardViewModel
import uz.yalla.client.feature.payment.add_employee.model.AddEmployeeViewModel
import uz.yalla.client.feature.payment.business_account.model.BusinessAccountViewModel
import uz.yalla.client.feature.payment.card_list.model.CardListViewModel
import uz.yalla.client.feature.payment.card_verification.model.CardVerificationViewModel
import uz.yalla.client.feature.payment.corporate_account.model.CorporateAccountViewModel
import uz.yalla.client.feature.payment.top_up_balance.model.TopUpViewModel
import uz.yalla.client.feature.payment.data.di.PaymentData

object Payment {
    private val viewModelModule = module {
        viewModelOf(::AddCardViewModel)
        viewModelOf(::CardListViewModel)
        viewModelOf(::CardVerificationViewModel)
        viewModelOf(::CorporateAccountViewModel)
        viewModelOf(::BusinessAccountViewModel)
        viewModelOf(::TopUpViewModel)
        viewModelOf(::AddEmployeeViewModel)
    }

    val modules = listOf(
        viewModelModule,
        *PaymentData.modules.toTypedArray()
    )
}