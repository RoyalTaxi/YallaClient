package uz.yalla.client.feature.map.presentation.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.yalla.client.core.common.new_map.core.MapViewModel
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.feature.domain.usecase.GetNotificationsCountUseCase
import uz.yalla.client.feature.map.domain.usecase.GetAddressNameUseCase
import uz.yalla.client.feature.map.domain.usecase.GetRoutingUseCase
import uz.yalla.client.feature.map.presentation.intent.MapEffect
import uz.yalla.client.feature.map.presentation.intent.MapState
import uz.yalla.client.feature.order.domain.usecase.order.GetActiveOrdersUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetSettingUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.profile.domain.usecase.GetMeUseCase


class MViewModel(
    internal val mapsViewModel: MapViewModel,
    internal val prefs: AppPreferences,
    internal val getMeUseCase: GetMeUseCase,
    internal val getAddressNameUseCase: GetAddressNameUseCase,
    internal val getShowOrderUseCase: GetShowOrderUseCase,
    internal val getRoutingUseCase: GetRoutingUseCase,
    internal val getActiveOrdersUseCase: GetActiveOrdersUseCase,
    internal val getNotificationsCountUseCase: GetNotificationsCountUseCase,
    internal val getSettingUseCase: GetSettingUseCase,
) : BaseViewModel(), LifeCycleAware {

    internal val mutableStateFlow = MutableStateFlow(MapState())
    val stateFlow: StateFlow<MapState> = mutableStateFlow.asStateFlow()

    internal val mutableSharedFlow = MutableSharedFlow<MapEffect>()
    val effectFlow: SharedFlow<MapEffect> = mutableSharedFlow.asSharedFlow()

    fun updateState(update: (MapState) -> MapState) {
        mutableStateFlow.value = update(mutableStateFlow.value)
    }

    fun launchEffect(effect: MapEffect) {
        viewModelScope.launch {
            mutableSharedFlow.emit(effect)
        }
    }

    override var scope: CoroutineScope? = null

    override fun onCreate() {
        super.onCreate()
        viewModelScope.launch {
            getMe()
            getNotificationsCount()
            getSettingConfig()
        }
    }

    override fun onStart() {
        super.onStart()
        scope = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob())
        scope?.launch { observeActiveOrders() }
        scope?.launch { observeMarkerState() }
        scope?.launch { observeLocations() }
        scope?.launch { observeActiveOrder() }
        scope?.launch { observeSheetCoordinator() }
        scope?.launch { observeRoute() }
        scope?.launch { observeInfoMarkers() }
        scope?.launch { observeNavigationButton() }
        scope?.launch { observeDriver() }
        scope?.launch { observeDrivers() }
    }
}
