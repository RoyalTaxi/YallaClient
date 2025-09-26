package uz.yalla.client.config

sealed class Config(val key: String) {
    data object EventCollection : Config("event_collection_enabled")
}