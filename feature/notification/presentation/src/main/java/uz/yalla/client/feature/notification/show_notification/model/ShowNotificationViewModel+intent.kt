package uz.yalla.client.feature.notification.show_notification.model

import uz.yalla.client.feature.notification.show_notification.intent.ShowNotificationIntent
import uz.yalla.client.feature.notification.show_notification.intent.ShowNotificationSideEffect

internal fun ShowNotificationViewModel.onIntent(intent: ShowNotificationIntent) = intent {
    when (intent) {
        ShowNotificationIntent.NavigateBack -> postSideEffect(ShowNotificationSideEffect.NavigateBack)
    }
}