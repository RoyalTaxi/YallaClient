package uz.yalla.client.feature.android.contact.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.contact.contact_us.model.ContactUsViewModel

object ContactViewModel {
    var module = module {
        viewModelOf(::ContactUsViewModel)
    }
}