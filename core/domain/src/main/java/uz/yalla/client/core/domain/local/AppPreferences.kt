package uz.yalla.client.core.domain.local

import kotlinx.coroutines.flow.Flow

interface AppPreferences {
    val locale: Flow<String>
    fun setLocale(value: String)

    val tokenType: Flow<String>
    fun setTokenType(value: String)

    val accessToken: Flow<String>
    fun setAccessToken(value: String)
}