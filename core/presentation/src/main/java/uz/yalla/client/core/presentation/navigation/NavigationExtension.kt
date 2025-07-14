package uz.yalla.client.core.presentation.navigation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val NavController.isCurrentDestinationActive: Boolean
    get() = currentBackStackEntry?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED) == true

fun NavController.safeNavigate(screen: String) {
    safeLaunch {
        withContext(Dispatchers.Main) {
            if (isCurrentDestinationActive) navigate(screen)
        }
    }
}

fun NavController.safeNavigate(screen: String, navOptions: NavOptions?) {
    safeLaunch {
        withContext(Dispatchers.Main) {
            if (isCurrentDestinationActive) navigate(screen, navOptions)
        }
    }
}

fun NavController.safePopBackStack() {
    safeLaunch {
        withContext(Dispatchers.Main) {
            if (isCurrentDestinationActive) popBackStack()
        }
    }
}

fun NavController.safeLaunch(block: suspend CoroutineScope.() -> Unit) {
    (this.context as? androidx.lifecycle.LifecycleOwner)?.lifecycleScope?.launch {
        block()
    }
}