package uz.ildam.technologies.yalla.android.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.ildam.technologies.yalla.android.ui.screens.add_card.AddCardViewModel
import uz.ildam.technologies.yalla.android.ui.screens.card_list.CardListViewModel
import uz.ildam.technologies.yalla.android.ui.screens.credentials.CredentialsViewModel
import uz.ildam.technologies.yalla.android.ui.screens.details.DetailsViewModel
import uz.ildam.technologies.yalla.android.ui.screens.history.HistoryViewModel
import uz.ildam.technologies.yalla.android.ui.screens.language.LanguageViewModel
import uz.ildam.technologies.yalla.android.ui.screens.login.LoginViewModel
import uz.ildam.technologies.yalla.android.ui.screens.map.MapViewModel
import uz.ildam.technologies.yalla.android.ui.screens.verification.VerificationViewModel
import uz.ildam.technologies.yalla.android.ui.sheets.select_from_map.SelectFromMapBottomSheetViewModel

object Navigation {

    private val viewModelModule = module {
        viewModelOf(::LanguageViewModel)
        viewModelOf(::LoginViewModel)
        viewModelOf(::VerificationViewModel)
        viewModelOf(::CredentialsViewModel)
        viewModelOf(::MapViewModel)
        viewModelOf(::HistoryViewModel)
        viewModelOf(::DetailsViewModel)
        viewModelOf(::SelectFromMapBottomSheetViewModel)
        viewModelOf(::AddCardViewModel)
        viewModelOf(::CardListViewModel)
    }

    val modules = listOf(viewModelModule)
}