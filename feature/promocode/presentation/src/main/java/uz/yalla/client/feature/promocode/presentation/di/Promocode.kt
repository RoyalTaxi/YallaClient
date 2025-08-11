package uz.yalla.client.feature.promocode.presentation.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.promocode.data.di.PromocodeData
import uz.yalla.client.feature.promocode.presentation.add_promocode.model.AddPromocodeViewModel

object Promocode {
    private var viewModelModule = module {
        viewModelOf(::AddPromocodeViewModel)
    }

    val modules = listOf(
        viewModelModule,
        *PromocodeData.modules.toTypedArray()
    )
}
