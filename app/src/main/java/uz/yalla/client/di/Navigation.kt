package uz.yalla.client.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.activity.MainActivity
import uz.yalla.client.activity.MainViewModel
import uz.yalla.client.connectivity.AndroidConnectivityObserver
import uz.yalla.client.connectivity.ConnectivityObserver
import uz.yalla.client.core.common.di.Common
import android.content.Context
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.maps.core.viewmodel.MapsViewModel
import uz.yalla.client.core.common.maps.core.manager.MapElementManager
import uz.yalla.client.core.common.maps.core.manager.MapIconManager
import uz.yalla.client.core.common.maps.core.manager.MapThemeManager
import uz.yalla.client.core.common.maps.core.handler.GenericMapConfigHandler
import uz.yalla.client.core.common.maps.core.controller.MapController
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.MapType
import uz.yalla.client.feature.auth.di.Auth
import uz.yalla.client.feature.contact.di.Contact
import uz.yalla.client.feature.history.di.History
import uz.yalla.client.feature.info.about_app.di.Info
import uz.yalla.client.feature.intro.di.Intro
import uz.yalla.client.feature.map.presentation.di.Map
import uz.yalla.client.feature.notification.di.Notifications
import uz.yalla.client.feature.order.presentation.di.Order
import uz.yalla.client.feature.payment.di.Payment
import uz.yalla.client.feature.places.di.Places
import uz.yalla.client.feature.profile.di.Profile
import uz.yalla.client.feature.promocode.presentation.di.Promocode
import uz.yalla.client.feature.registration.presentation.di.Registration
import uz.yalla.client.feature.setting.di.Setting

object Navigation {

    private val androidServices = module {
        single<ConnectivityObserver> { AndroidConnectivityObserver(get()) }
    }

    private val viewModelModule = module {
        viewModelOf(::MainViewModel)
        scope<MainActivity> {
            scoped<MapsViewModel> {
                val appContext = get<Context>()
                val appPreferences = get<AppPreferences>()

                // Resolve platform-specific managers from the current preference
                val iconManager = get<MapIconManager>()
                val elementManager = get<MapElementManager>()
                val themeManager = get<MapThemeManager>()

                // Capture a reference to the view model after creation; use it in the config handler callback
                var vmRef: MapsViewModel? = null
                val configHandler = GenericMapConfigHandler(
                    context = appContext,
                    coroutineScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main.immediate + kotlinx.coroutines.SupervisorJob()),
                    themeManager = themeManager,
                    iconManager = iconManager,
                    onConfigurationChanged = { vmRef?.onExternalConfigurationChanged() }
                )

                val vm = MapsViewModel(
                    appContext = appContext,
                    appPreferences = appPreferences,
                    iconManager = iconManager,
                    elementManager = elementManager,
                    themeManager = themeManager,
                    configHandler = configHandler,
                    mapController = get<MapController>()
                )
                vmRef = vm
                vm
            }
        }
    }

    val modules = listOf(
        androidServices,
        viewModelModule,
        *Common.modules.toTypedArray(),
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
        *Notifications.modules.toTypedArray(),
        *Promocode.modules.toTypedArray(),
    )
}
