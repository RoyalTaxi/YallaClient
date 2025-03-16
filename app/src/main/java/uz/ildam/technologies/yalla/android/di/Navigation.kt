package uz.ildam.technologies.yalla.android.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.ildam.technologies.yalla.android.activity.MainViewModel
import uz.ildam.technologies.yalla.android.connectivity.AndroidConnectivityObserver
import uz.ildam.technologies.yalla.android.connectivity.ConnectivityObserver
import uz.yalla.client.feature.android.auth.di.Auth
import uz.yalla.client.feature.android.contact.di.Contact
import uz.yalla.client.feature.android.history.di.History
import uz.yalla.client.feature.android.info.di.Info
import uz.yalla.client.feature.android.intro.di.Intro
import uz.yalla.client.feature.android.payment.di.Payment
import uz.yalla.client.feature.android.places.di.Places
import uz.yalla.client.feature.android.profile.di.Profile
import uz.yalla.client.feature.android.setting.di.Setting
import uz.yalla.client.feature.map.presentation.di.Map
import uz.yalla.client.feature.order.presentation.di.Order
import uz.yalla.client.feature.registration.presentation.di.Registration

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
        *Intro.modules.toTypedArray(),
        *Auth.modules.toTypedArray(),
        *Registration.modules.toTypedArray(),
        *Map.modules.toTypedArray(),
        *Order.modules.toTypedArray(),
        *Payment.modules.toTypedArray(),
        *Places.modules.toTypedArray(),
        *Profile.modules.toTypedArray(),
        *History.modules.toTypedArray(),
        *Info.modules.toTypedArray(),
        *Setting.modules.toTypedArray(),
        *Contact.modules.toTypedArray()
    )
}