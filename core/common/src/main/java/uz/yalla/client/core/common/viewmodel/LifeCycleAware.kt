package uz.yalla.client.core.common.viewmodel

import kotlinx.coroutines.CoroutineScope

interface LifeCycleAware {
    val scope: CoroutineScope
    fun onAppear()
    fun onDisappear()
}