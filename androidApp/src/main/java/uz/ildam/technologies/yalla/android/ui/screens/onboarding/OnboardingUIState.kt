package uz.ildam.technologies.yalla.android.ui.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingUIState(
    @DrawableRes val image: Int,
    @StringRes val title: Int,
    @StringRes val desc: Int,
)
