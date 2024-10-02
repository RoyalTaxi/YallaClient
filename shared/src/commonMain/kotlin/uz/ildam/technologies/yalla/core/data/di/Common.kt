package uz.ildam.technologies.yalla.core.data.di

import uz.ildam.technologies.yalla.feature.auth.data.di.Auth

object Common {
    val modules = listOf(
        Network.module,
        *Auth.modules.toTypedArray()
    )
}