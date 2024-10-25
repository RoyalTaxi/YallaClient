package uz.ildam.technologies.yalla.android.ui.screens.onboarding

sealed interface OnboardingIntent {
    data object Swipe : OnboardingIntent
    data object NavigateNext : OnboardingIntent
}