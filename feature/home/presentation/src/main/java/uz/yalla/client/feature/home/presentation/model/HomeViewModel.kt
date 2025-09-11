package uz.yalla.client.feature.home.presentation.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.map.core.model.MapViewModel
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.feature.domain.usecase.GetNotificationsCountUseCase
import uz.yalla.client.feature.home.domain.usecase.GetAddressNameUseCase
import uz.yalla.client.feature.home.domain.usecase.GetRoutingUseCase
import uz.yalla.client.feature.home.presentation.intent.HomeEffect
import uz.yalla.client.feature.home.presentation.intent.HomeState
import uz.yalla.client.feature.home.presentation.navigation.OrderSheet
import uz.yalla.client.feature.order.domain.usecase.order.GetActiveOrdersUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetSettingUseCase
import uz.yalla.client.feature.order.domain.usecase.order.GetShowOrderUseCase
import uz.yalla.client.feature.profile.domain.usecase.GetMeUseCase

class HomeViewModel(
    internal val mapsViewModel: MapViewModel,
    internal val prefs: AppPreferences,
    internal val staticPreferences: StaticPreferences,
    internal val getMeUseCase: GetMeUseCase,
    internal val getAddressNameUseCase: GetAddressNameUseCase,
    internal val getShowOrderUseCase: GetShowOrderUseCase,
    internal val getRoutingUseCase: GetRoutingUseCase,
    internal val getActiveOrdersUseCase: GetActiveOrdersUseCase,
    internal val getNotificationsCountUseCase: GetNotificationsCountUseCase,
    internal val getSettingUseCase: GetSettingUseCase,
) : BaseViewModel(), ContainerHost<HomeState, HomeEffect>, LifeCycleAware {

    override val container: Container<HomeState, HomeEffect> = container(HomeState.INITIAL)
    override var scope: CoroutineScope? = null

    private val _sheet = kotlinx.coroutines.flow.MutableStateFlow<OrderSheet?>(OrderSheet.Main)
    val sheetFlow: kotlinx.coroutines.flow.StateFlow<OrderSheet?> = _sheet.asStateFlow()

    fun setSheet(sheet: OrderSheet?) {
        _sheet.value = sheet
    }

    override fun onCreate() {
        super.onCreate()
        viewModelScope.launch { getMe() }
        viewModelScope.launch { getNotificationsCount() }
        viewModelScope.launch { getSettingConfig() }
    }

    override fun onStart() {
        super.onStart()
        scope = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob())

        scope?.launch { pollActiveOrder() }
        scope?.launch { pollActiveOrders() }
        scope?.launch { observeMarkerState() }
        scope?.launch { observeLocations() }
        scope?.launch { observeSheetCoordinator() }
        scope?.launch { observeViewPadding() }
        scope?.launch { observeRoute() }
        scope?.launch { observeInfoMarkers() }
        scope?.launch { observeNavigationButton() }
        scope?.launch { observeOrder() }
        scope?.launch { observeDrivers() }
        scope?.launch { observeSheetComputation() }
    }
}