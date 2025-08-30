package uz.yalla.client.feature.info.about_app.model

import uz.yalla.client.feature.info.about_app.intent.AboutAppIntent
import uz.yalla.client.feature.info.about_app.intent.AboutAppSideEffect

fun AboutAppViewModel.onIntent(intent: AboutAppIntent) = intent {
    when (intent) {
        AboutAppIntent.NavigateBack -> postSideEffect(AboutAppSideEffect.NavigateBack)
        is AboutAppIntent.NavigateToWeb -> postSideEffect(
            AboutAppSideEffect.NavigateWeb(
                title = intent.title,
                url = intent.url
            )
        )
    }
}