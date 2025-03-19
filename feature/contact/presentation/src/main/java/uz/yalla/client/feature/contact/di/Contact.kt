package uz.yalla.client.feature.contact.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.contact.contact_us.model.ContactUsViewModel

object Contact {
    private var viewModelModule = module {
        viewModelOf(::ContactUsViewModel)
    }

    val modules = listOf(viewModelModule)
}