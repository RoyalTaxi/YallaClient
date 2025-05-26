package uz.yalla.client.feature.order.presentation.feedback.view

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

object FeedbackSheetChannel {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val activeLifecycles = mutableSetOf<LifecycleOwner>()

    private val _intentFlow = MutableSharedFlow<FeedbackSheetIntent>()
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

    fun sendIntent(intent: FeedbackSheetIntent) {
        if (hasActiveLifecycles()) {
            scope.launch {
                _intentFlow.emit(intent)
            }
        }
    }
}