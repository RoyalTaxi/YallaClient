package uz.yalla.client.feature.order.presentation.main.view

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import uz.yalla.client.core.domain.model.Destination
import uz.yalla.client.core.domain.model.SelectedLocation

object MainSheetChannel {
    private val scope = CoroutineScope(Dispatchers.Main)
    val intentChannel = MutableSharedFlow<MainSheetIntent>()
    val actionChannel = MutableSharedFlow<MainSheetAction>()

    fun sendIntent(intent: MainSheetIntent) = scope.launch {
        intentChannel.emit(intent)
    }

    fun setDestination(destinations: List<Destination>) = scope.launch {
        actionChannel.emit(MainSheetAction.SetDestination(destinations))
    }

    fun setLocation(location: SelectedLocation) = scope.launch {
        actionChannel.emit(MainSheetAction.SetLocation(location))
    }
}