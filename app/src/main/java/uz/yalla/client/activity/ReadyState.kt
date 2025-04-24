package uz.yalla.client.activity

sealed class ReadyState {
    data object Loading : ReadyState()
    data object Ready : ReadyState()
    data object Failed : ReadyState()
}