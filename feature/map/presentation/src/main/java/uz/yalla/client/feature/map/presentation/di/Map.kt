package uz.yalla.client.feature.map.presentation.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.map.data.di.MapData
import uz.yalla.client.feature.map.presentation.model.MapViewModel
import uz.yalla.client.feature.map.presentation.new_version.model.MViewModel

object Map {
    private val viewModelModule = module {
        viewModelOf(::MapViewModel)
        viewModelOf(::MViewModel)
    }

    val modules = listOf(
        viewModelModule,
        *MapData.modules.toTypedArray()
    )
}