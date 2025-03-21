package uz.yalla.client.core.common.marker


sealed interface YallaMarkerState {
    data object LOADING : YallaMarkerState
    data class IDLE(val title: String?, val timeout: Int?) : YallaMarkerState
    data class Searching(val title: String?) : YallaMarkerState
}