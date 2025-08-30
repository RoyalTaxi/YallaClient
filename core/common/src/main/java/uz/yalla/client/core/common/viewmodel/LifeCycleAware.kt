package uz.yalla.client.core.common.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

interface LifeCycleAware {
    val scope: CoroutineScope?
    fun onCreate() {}
    fun onStart() {}
    fun onResume() {}
    fun onPause() {}
    fun onStop() {
        scope?.cancel()
    }

    fun onDestroy() {}
}