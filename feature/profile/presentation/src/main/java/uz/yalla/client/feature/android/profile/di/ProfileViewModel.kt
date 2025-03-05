package uz.yalla.client.feature.android.profile.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.profile.edit_profile.model.EditProfileViewModel

object ProfileViewModel {
    var module = module {
        viewModelOf(::EditProfileViewModel)
    }
}