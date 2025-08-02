package uz.yalla.client.core.common.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Utility class for debouncing operations.
 * Helps prevent excessive updates by delaying execution until input stabilizes.
 *
 * @param delayMs The delay in milliseconds before executing the action
 */
class Debouncer(private val delayMs: Long) {
    private var job: Job? = null
    
    /**
     * Debounces the given action, canceling any previous pending action.
     *
     * @param action The suspend function to execute after the delay
     */
    fun debounce(action: suspend () -> Unit) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            delay(delayMs)
            action()
        }
    }
    
    /**
     * Cancels any pending debounced action.
     */
    fun cancel() {
        job?.cancel()
        job = null
    }
}