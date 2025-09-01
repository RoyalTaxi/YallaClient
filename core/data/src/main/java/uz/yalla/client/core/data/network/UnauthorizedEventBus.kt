package uz.yalla.client.core.data.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object UnauthorizedEventBus {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 64)
    val events: SharedFlow<Unit> = _events

    fun emit() {
        _events.tryEmit(Unit)
    }
}

