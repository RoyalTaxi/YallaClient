package uz.ildam.technologies.yalla.android.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.ildam.technologies.yalla.android.activity.MainViewModel
import uz.ildam.technologies.yalla.android.connectivity.AndroidConnectivityObserver
import uz.ildam.technologies.yalla.android.connectivity.ConnectivityObserver
import uz.ildam.technologies.yalla.android.ui.screens.cancel_reason.CancelReasonViewModel
import uz.ildam.technologies.yalla.android.ui.screens.map.MapViewModel
import uz.ildam.technologies.yalla.android.ui.sheets.search_address.SearchByNameBottomSheetViewModel
import uz.ildam.technologies.yalla.android.ui.sheets.select_from_map.SelectFromMapBottomSheetViewModel
import uz.yalla.client.feature.android.auth.di.AuthViewModel
import uz.yalla.client.feature.android.contact.di.ContactViewModel
import uz.yalla.client.feature.android.history.di.HistoryViewModel
import uz.yalla.client.feature.android.info.di.InfoViewModel
import uz.yalla.client.feature.android.intro.di.IntroViewModel
import uz.yalla.client.feature.android.payment.di.PaymentViewModel
import uz.yalla.client.feature.android.places.di.PlacesViewModel
import uz.yalla.client.feature.android.profile.di.ProfileViewModel
import uz.yalla.client.feature.android.registration.di.RegistrationViewModel
import uz.yalla.client.feature.android.setting.di.SettingViewModel

object Navigation {

    private val androidServices = module {
        single<ConnectivityObserver> { AndroidConnectivityObserver(get()) }
    }

    private val viewModelModule = module {
        viewModelOf(::MapViewModel)
        viewModelOf(::CancelReasonViewModel)
        viewModelOf(::MainViewModel)

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
        PlacesViewModel.module,
        ProfileViewModel.module,
        HistoryViewModel.module,
        InfoViewModel.module,
        SettingViewModel.module,
        ProfileViewModel.module,
        HistoryViewModel.module,
        ContactViewModel.module
    )
}