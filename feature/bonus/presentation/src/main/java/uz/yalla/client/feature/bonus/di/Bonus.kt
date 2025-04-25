package uz.yalla.client.feature.bonus.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.bonus.bonus_account.model.BonusAccountViewModel

object Bonus {
    private var viewModelModule = module {
        viewModelOf(::BonusAccountViewModel)
    }

    var modules = listOf(
        viewModelModule
    )
}