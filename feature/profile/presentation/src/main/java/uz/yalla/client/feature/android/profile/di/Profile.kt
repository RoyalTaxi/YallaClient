package uz.yalla.client.feature.android.profile.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.profile.edit_profile.model.EditProfileViewModel
import uz.yalla.client.feature.profile.data.di.ProfileData

object Profile {
    private var viewModelModule = module {
        viewModelOf(::EditProfileViewModel)
    }

    val modules = listOf(
        viewModelModule,
        *ProfileData.modules.toTypedArray()
    )
}