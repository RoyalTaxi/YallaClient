package uz.yalla.client.feature.android.intro.onboarding.view

internal sealed interface OnboardingIntent {
    data object Swipe : OnboardingIntent
}