package uz.ildam.technologies.yalla.android.di

import AboutAppViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.ildam.technologies.yalla.android.activity.MainViewModel
import uz.ildam.technologies.yalla.android.connectivity.AndroidConnectivityObserver
import uz.ildam.technologies.yalla.android.connectivity.ConnectivityObserver
import uz.ildam.technologies.yalla.android.ui.screens.add_card.AddCardViewModel
import uz.ildam.technologies.yalla.android.ui.screens.cancel_reason.CancelReasonViewModel
import uz.ildam.technologies.yalla.android.ui.screens.card_list.CardListViewModel
import uz.ildam.technologies.yalla.android.ui.screens.card_verification.CardVerificationViewModel
import uz.ildam.technologies.yalla.android.ui.screens.contact_us.ContactUsViewModel
import uz.ildam.technologies.yalla.android.ui.screens.details.DetailsViewModel
import uz.ildam.technologies.yalla.android.ui.screens.edit_profile.EditProfileViewModel
import uz.ildam.technologies.yalla.android.ui.screens.history.HistoryViewModel
import uz.ildam.technologies.yalla.android.ui.screens.map.MapViewModel
import uz.ildam.technologies.yalla.android.ui.screens.settings.SettingsViewModel
import uz.ildam.technologies.yalla.android.ui.sheets.search_address.SearchByNameBottomSheetViewModel
import uz.ildam.technologies.yalla.android.ui.sheets.select_from_map.SelectFromMapBottomSheetViewModel
import uz.yalla.client.feature.android.places.di.AddressModuleViewModel
import uz.yalla.client.feature.android.auth.di.AuthViewModel
import uz.yalla.client.feature.android.intro.di.IntroViewModel
import uz.yalla.client.feature.android.payment.di.PaymentViewModel
import uz.yalla.client.feature.android.registration.di.RegistrationViewModel

object Navigation {

    private val androidServices = module {
        single<ConnectivityObserver> { AndroidConnectivityObserver(get()) }
    }

    private val viewModelModule = module {
        viewModelOf(::MapViewModel)
        viewModelOf(::HistoryViewModel)
        viewModelOf(::DetailsViewModel)
        viewModelOf(::AddCardViewModel)
        viewModelOf(::CardListViewModel)
        viewModelOf(::CardVerificationViewModel)
        viewModelOf(::CancelReasonViewModel)
        viewModelOf(::EditProfileViewModel)
        viewModelOf(::MainViewModel)
        viewModelOf(::AboutAppViewModel)
        viewModelOf(::ContactUsViewModel)
        viewModelOf(::SettingsViewModel)


        viewModelOf(::SelectFromMapBottomSheetViewModel)
        viewModelOf(::SearchByNameBottomSheetViewModel)
    }

    val modules = listOf(
        androidServices,
        viewModelModule,
        AuthViewModel.module,
        IntroViewModel.module,
        RegistrationViewModel.module,
        PaymentViewModel.module,
        AddressModuleViewModel.module
    )
}