package uz.yalla.client.core.common.map.static.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.map.static.intent.StaticMapEffect
import uz.yalla.client.core.common.map.static.intent.StaticMapIntent
import uz.yalla.client.core.common.map.static.intent.StaticMapState
import uz.yalla.client.core.common.viewmodel.BaseViewModel

class StaticMapViewModel : BaseViewModel(), ContainerHost<StaticMapState, StaticMapEffect> {
    override val container: Container<StaticMapState, StaticMapEffect> = container(StaticMapState.INITIAL)

    val isMapReady: Flow<Boolean> =
        container.stateFlow
            .map { it.isMapReady }
            .distinctUntilChanged()
}