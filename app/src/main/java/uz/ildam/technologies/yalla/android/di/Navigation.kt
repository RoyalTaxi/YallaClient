package uz.ildam.technologies.yalla.android.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.ildam.technologies.yalla.android.activity.MainViewModel
import uz.ildam.technologies.yalla.android.connectivity.AndroidConnectivityObserver
import uz.ildam.technologies.yalla.android.connectivity.ConnectivityObserver
import uz.yalla.client.feature.android.auth.di.Auth
import uz.yalla.client.feature.android.contact.di.ContactViewModel
import uz.yalla.client.feature.android.history.di.HistoryViewModel
import uz.yalla.client.feature.android.info.di.InfoViewModel
import uz.yalla.client.feature.android.intro.di.IntroViewModel
import uz.yalla.client.feature.android.payment.di.Payment
import uz.yalla.client.feature.android.places.di.PlacesViewModel
import uz.yalla.client.feature.android.profile.di.ProfileViewModel
import uz.yalla.client.feature.android.registration.di.RegistrationViewModel
import uz.yalla.client.feature.android.setting.di.SettingViewModel
import uz.yalla.client.feature.map.presentation.di.Map

object Navigation {

    private val androidServices = module {
        single<ConnectivityObserver> { AndroidConnectivityObserver(get()) }
    }

    private val viewModelModule = module {
        viewModelOf(::MainViewModel)
    }

    val modules = listOf(
        androidServices,
        viewModelModule,
        *Auth.modules.toTypedArray(),
        IntroViewModel.module,
        RegistrationViewModel.module,
        *Map.modules.toTypedArray(),
        *Payment.modules.toTypedArray(),
        *PlacesViewModel.modules.toTypedArray(),
        ProfileViewModel.module,
        HistoryViewModel.module,
        InfoViewModel.module,
        *SettingViewModel.modules.toTypedArray(),
        HistoryViewModel.module,
        ContactViewModel.module
    )
}