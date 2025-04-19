package uz.yalla.client.feature.profile.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.profile.data.di.ProfileData
import uz.yalla.client.feature.profile.edit_profile.model.EditProfileViewModel

object Profile {
    private var viewModelModule = module {
        viewModelOf(::EditProfileViewModel)
    }

    val modules = listOf(
        viewModelModule,
        *ProfileData.modules.toTypedArray()
    )
}