package uz.yalla.client.feature.places.place.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import uz.yalla.client.core.common.viewmodel.BaseViewModel
import uz.yalla.client.core.common.viewmodel.LifeCycleAware
import uz.yalla.client.core.domain.model.type.PlaceType
import uz.yalla.client.feature.order.domain.usecase.DeleteOnePlaceUseCase
import uz.yalla.client.feature.order.domain.usecase.FindOnePlaceUseCase
import uz.yalla.client.feature.order.domain.usecase.PostOnePlaceUseCase
import uz.yalla.client.feature.order.domain.usecase.UpdateOnePlaceUseCase
import uz.yalla.client.feature.places.place.intent.PlaceSideEffect
import uz.yalla.client.feature.places.place.intent.PlaceState

internal class PlaceViewModel(
    internal val id: Int?,
    internal val type: PlaceType,
    internal val findOnePlaceUseCase: FindOnePlaceUseCase,
    internal val postOnePlaceUseCase: PostOnePlaceUseCase,
    internal val updateOnePlaceUseCase: UpdateOnePlaceUseCase,
    internal val deleteOnePlaceUseCase: DeleteOnePlaceUseCase
) : BaseViewModel(), LifeCycleAware, ContainerHost<PlaceState, PlaceSideEffect> {

    override val container: Container<PlaceState, PlaceSideEffect> =
        container(PlaceState.INITIAL)

    override var scope: CoroutineScope? = null

    override fun onCreate() {
        super.onCreate()
        viewModelScope.launch {
            if (id != null) findOneAddress(id)
            updateType(type)
        }
    }

    override fun onStart() {
        super.onStart()
        scope = CoroutineScope(viewModelScope.coroutineContext + SupervisorJob())
    }
}