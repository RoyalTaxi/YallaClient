package uz.yalla.client.feature.intro.onboarding.view

internal sealed interface OnboardingIntent {
    data object Swipe : OnboardingIntent
}