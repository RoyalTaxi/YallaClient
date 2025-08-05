package uz.yalla.client.feature.map.presentation.new_version.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
    internal val mapsViewModel: MapsViewModel,
    internal val prefs: AppPreferences,
    internal val staticPrefs: StaticPreferences,
    internal val getMeUseCase: GetMeUseCase,
    internal val getAddressNameUseCase: GetAddressNameUseCase,
    internal val getShowOrderUseCase: GetShowOrderUseCase,
    internal val getRoutingUseCase: GetRoutingUseCase,
    internal val getActiveOrdersUseCase: GetActiveOrdersUseCase,
    internal val getNotificationsCountUseCase: GetNotificationsCountUseCase,
    internal val getSettingUseCase: GetSettingUseCase,
) : BaseViewModel() {

    private val supervisorJob = SupervisorJob()
    internal val _stateFlow = MutableStateFlow(MapState())
    val stateFlow: StateFlow<MapState> = _stateFlow.asStateFlow()

    internal val _effectFlow = MutableSharedFlow<MapEffect>()
    val effectFlow: SharedFlow<MapEffect> = _effectFlow.asSharedFlow()

    fun updateState(update: (MapState) -> MapState) {
        _stateFlow.value = update(_stateFlow.value)
    }

    suspend fun emitEffect(effect: MapEffect) {
        _effectFlow.emit(effect)
    }

    fun launchEffect(effect: MapEffect) {
        viewModelScope.launch {
            _effectFlow.emit(effect)
        }
    }

    fun <T> launch(block: suspend () -> T) {
        viewModelScope.launch {
            block()
        }
    }

    internal var cancelable = arrayOf<Job>()
    internal val observerScope = CoroutineScope(viewModelScope.coroutineContext + supervisorJob)

    internal var hasInjectedOnceInThisSession = false

    init {
        startObserve()
        getMe()
        getNotificationsCount()
        getSettingConfig()
    }

    override fun onCleared() {
        super.onCleared()
        stopObserve()
    }
}
