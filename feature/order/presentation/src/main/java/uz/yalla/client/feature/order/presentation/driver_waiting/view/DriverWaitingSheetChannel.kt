package uz.yalla.client.feature.order.presentation.driver_waiting.view

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import uz.yalla.client.feature.order.presentation.on_the_ride.view.OnTheRideSheetIntent

object DriverWaitingSheetChannel {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val activeLifecycles = mutableSetOf<LifecycleOwner>()

    private val _intentFlow = MutableSharedFlow<DriverWaitingSheetIntent>()
    val intentFlow = _intentFlow.asSharedFlow()

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

    fun sendIntent(intent: DriverWaitingSheetIntent) {
        if (hasActiveLifecycles()) {
            scope.launch {
                _intentFlow.emit(intent)
            }
        }
    }
}