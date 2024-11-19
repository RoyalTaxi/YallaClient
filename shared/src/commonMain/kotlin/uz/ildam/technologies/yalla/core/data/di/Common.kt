package uz.ildam.technologies.yalla.core.data.di

import uz.ildam.technologies.yalla.feature.auth.data.di.Auth
import uz.ildam.technologies.yalla.feature.history.data.di.History
import uz.ildam.technologies.yalla.feature.map.data.di.Map
import uz.ildam.technologies.yalla.feature.order.data.di.Order

object Common {
    val modules = listOf(
        Network.module,
        *Auth.modules.toTypedArray(),
        *Map.modules.toTypedArray(),
        *Order.modules.toTypedArray(),
        *History.modules.toTypedArray()
    )
}