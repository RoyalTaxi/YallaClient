package uz.yalla.client.core.common.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.core.parameter.parametersOf
import uz.yalla.client.core.common.sheet.select_from_map.intent.SelectFromMapViewValue
import uz.yalla.client.core.domain.model.MapPoint
import uz.yalla.client.core.common.map.extended.model.MapViewModel
import uz.yalla.client.core.common.map.lite.model.LiteMapViewModel
import uz.yalla.client.core.common.map.static.model.StaticMapViewModel
import uz.yalla.client.core.common.sheet.search_address.DualAddressViewModel
import uz.yalla.client.core.common.sheet.search_address.SingleAddressViewModel
import uz.yalla.client.core.common.sheet.select_from_map.model.SelectFromMapViewModel

object Common {
    val module = module {
        singleOf(::MapViewModel)
        viewModelOf(::SingleAddressViewModel)
        viewModelOf(::DualAddressViewModel)
        
        viewModel { params ->
            val viewValue: SelectFromMapViewValue = params.get()
            val initialLocation: MapPoint? = params.getOrNull()
            SelectFromMapViewModel(
                viewValue = viewValue,
                liteMapViewModel = get { parametersOf(initialLocation) },
                getTariffsUseCase = get(),
                getAddressNameUseCase = get(),
            )
        }
        viewModel { parameter ->
            LiteMapViewModel(
                appContext = androidContext(),
                initialLocation = parameter.getOrNull()
            )
        }
        viewModelOf(::StaticMapViewModel)

    }

    val modules = listOf(
        module
    )
}
