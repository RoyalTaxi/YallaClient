package uz.yalla.client.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.activity.MainViewModel
import uz.yalla.client.connectivity.AndroidConnectivityObserver
import uz.yalla.client.connectivity.ConnectivityObserver
import uz.yalla.client.core.common.di.Common
import uz.yalla.client.feature.auth.di.Auth
import uz.yalla.client.feature.bonus.di.Bonus
import uz.yalla.client.feature.contact.di.Contact
import uz.yalla.client.feature.history.di.History
import uz.yalla.client.feature.info.about_app.di.Info
import uz.yalla.client.feature.intro.di.Intro
import uz.yalla.client.feature.payment.di.Payment
import uz.yalla.client.feature.places.di.Places
import uz.yalla.client.feature.di.Profile
import uz.yalla.client.feature.setting.di.Setting
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
        Common.module,
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
        *Contact.modules.toTypedArray(),
        *Bonus.modules.toTypedArray()
    )
}