package uz.yalla.client.feature.order.presentation.main.view

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.Location

object MainSheetChannel {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val activeLifecycles = mutableSetOf<LifecycleOwner>()

    private val _intentFlow = MutableSharedFlow<MainSheetIntent>()
    val intentFlow = _intentFlow.asSharedFlow()

    private val _actionFlow = MutableSharedFlow<MainSheetAction>()
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

    fun sendIntent(intent: MainSheetIntent) {
        if (hasActiveLifecycles()) {
            scope.launch {
                _intentFlow.emit(intent)
            }
        }
    }

    fun setDestination(destinations: List<Destination>) {
        if (hasActiveLifecycles()) {
            scope.launch {
                _actionFlow.emit(MainSheetAction.SetDestination(destinations))
            }
        }
    }

    fun setLocation(location: Location) {
        if (hasActiveLifecycles()) {
            scope.launch {
                _actionFlow.emit(MainSheetAction.SetLocation(location))
            }
        }
    }

    fun setBonusVisibility(isVisible: Boolean) {
        if (hasActiveLifecycles()) {
            scope.launch {
                _actionFlow.emit(MainSheetAction.SetBonusInfoVisibility(isVisible))
            }
        }
    }
}