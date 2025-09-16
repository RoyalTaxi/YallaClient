package uz.yalla.client.feature.order.presentation.no_service.view

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import uz.yalla.client.core.domain.model.Location

object NoServiceSheetChannel {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val activeLifecycles = mutableSetOf<LifecycleOwner>()

    private val _intentFlow = MutableSharedFlow<NoServiceSheetIntent>()
    val intentFlow = _intentFlow.asSharedFlow()

    // Buffer actions so Home can emit before NoService sheet registers.
    private val _actionFlow = MutableSharedFlow<NoServiceSheetAction>(replay = 4, extraBufferCapacity = 16)
    val actionFlow = _actionFlow.asSharedFlow()

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            activeLifecycles.add(owner)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            activeLifecycles.remove(owner)
        }
    }

    fun register(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            activeLifecycles.add(lifecycleOwner)
        }
    }

    private fun hasActiveLifecycles(): Boolean {
        return activeLifecycles.any {
            it.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)
        }
    }

    fun sendIntent(intent: NoServiceSheetIntent) {
        if (hasActiveLifecycles()) {
            scope.launch { _intentFlow.emit(intent) }
        }
    }

    // Always emit; replay ensures delivery when collector attaches.
    fun setLocation(location: Location) {
        scope.launch { _actionFlow.emit(NoServiceSheetAction.SetLocation(location)) }
    }
}
