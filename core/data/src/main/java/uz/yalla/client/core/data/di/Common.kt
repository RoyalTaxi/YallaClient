package uz.yalla.client.core.data.di

object Common {
    val modules = listOf(
        Local.module,
        Network.module,
    )
}