package uz.yalla.client.feature.map.presentation.new_version.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.maps.MapsViewModel
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.feature.domain.usecase.GetNotificationsCountUseCase
import uz.yalla.client.feature.map.domain.usecase.GetAddressNameUseCase
import uz.yalla.client.feature.map.domain.usecase.GetRoutingUseCase
import uz.yalla.client.feature.map.presentation.new_version.intent.MapEffect
import uz.yalla.client.feature.map.presentation.new_version.intent.MapState
import uz.yalla.client.feature.order.domain.usecase.order.GetActiveOrdersUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetSettingUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.profile.domain.usecase.GetMeUseCase


class MViewModel(
    internal val prefs: AppPreferences,
    internal val staticPrefs: StaticPreferences,
    internal val mapsViewModel: MapsViewModel,
    internal val getMeUseCase: GetMeUseCase,
    internal val getAddressNameUseCase: GetAddressNameUseCase,
    internal val getShowOrderUseCase: GetShowOrderUseCase,
    internal val getRoutingUseCase: GetRoutingUseCase,
    internal val getActiveOrdersUseCase: GetActiveOrdersUseCase,
    internal val getNotificationsCountUseCase: GetNotificationsCountUseCase,
    internal val getSettingUseCase: GetSettingUseCase,
) : BaseViewModel(), LifeCycleAware, ContainerHost<MapState, MapEffect> {

    private val supervisorJob = SupervisorJob()
    override val container: Container<MapState, MapEffect> = container(MapState())
    internal var cancelable = arrayOf<Job>()
    internal val observerScope = CoroutineScope(viewModelScope.coroutineContext + supervisorJob)

    internal var hasInjectedOnceInThisSession = false

    init {
        startObserve()
    }

    override fun onAppear() {
        getMe()
        getNotificationsCount()
        getSettingConfig()
    }

    override fun onDisappear() {
        stopObserve()
    }
}
