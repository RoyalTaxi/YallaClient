package uz.yalla.client.feature.android.auth.verification.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uz.yalla.client.feature.android.auth.verification.model.VerificationViewModel

object VerificationViewModel {
    val model = module {
        viewModelOf(::VerificationViewModel)
    }

}