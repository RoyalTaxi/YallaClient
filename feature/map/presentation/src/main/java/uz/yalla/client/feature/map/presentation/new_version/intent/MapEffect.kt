package uz.yalla.client.feature.map.presentation.new_version.intent

sealed interface MapEffect {
    data object EnableLocation : MapEffect
    data object GrantLocation : MapEffect
}