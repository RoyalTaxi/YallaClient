package uz.ildam.technologies.yalla.android.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions


val NavController.canGoBack: Boolean
    get() = this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED


fun NavController.safeNavigate(screen: String) {
    if (canGoBack) this.navigate(screen)
}

fun NavController.safeNavigate(screen: String, navOptions: NavOptions?) {
    if (canGoBack) this.navigate(screen, navOptions)
}

fun NavController.safePopBackStack() {
    if (canGoBack) this.popBackStack()
}