package uz.yalla.client.feature.info.about_app.model

import kotlinx.coroutines.flow.first
import uz.yalla.client.core.domain.model.MapType
import uz.yalla.client.feature.info.about_app.intent.AboutAppIntent
import uz.yalla.client.feature.info.about_app.intent.AboutAppSideEffect

fun AboutAppViewModel.onIntent(intent: AboutAppIntent) = intent {
    when (intent) {
        AboutAppIntent.NavigateBack -> postSideEffect(AboutAppSideEffect.NavigateBack)
        AboutAppIntent.ChangeMap -> prefs.setMapType(
            when (prefs.mapType.first()) {
                MapType.Google -> MapType.Libre
                MapType.Libre -> MapType.Google
            }
        )

        is AboutAppIntent.NavigateToWeb -> postSideEffect(
            AboutAppSideEffect.NavigateWeb(
                title = intent.title,
                url = intent.url
            )
        )
    }
}