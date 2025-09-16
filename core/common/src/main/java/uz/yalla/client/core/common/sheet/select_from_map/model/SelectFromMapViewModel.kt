package uz.yalla.client.core.common.sheet.select_from_map.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.map.lite.model.LiteMapViewModel
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapEffect
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapState
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapViewValue
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.feature.home.domain.usecase.GetAddressNameUseCase
import uz.yalla.client.feature.order.domain.usecase.tariff.GetTariffsUseCase

class SelectFromMapViewModel(
    internal val viewValue: SelectFromMapViewValue,
    internal val liteMapViewModel: LiteMapViewModel,
    internal val getTariffsUseCase: GetTariffsUseCase,
    internal val getAddressNameUseCase: GetAddressNameUseCase
) : BaseViewModel(), LifeCycleAware, ContainerHost<SelectFromMapState, SelectFromMapEffect> {
    override val container: Container<SelectFromMapState, SelectFromMapEffect> =
        container(SelectFromMapState(viewValue = viewValue))

    override var scope: CoroutineScope? = null

    override fun onStart() {
        super.onStart()
        scope = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob())
        scope?.launch { observeMarkerState() }
    }
}