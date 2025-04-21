package uz.yalla.client.core.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath
import uz.yalla.client.core.domain.local.AppPreferences

const val dataStoreFileName = "prefs.preferences_pb"

internal class AppPreferencesImpl(context: Context) : AppPreferences {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val store: DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            context.filesDir.resolve(dataStoreFileName).absolutePath.toPath()
        }
    )

    fun <T> get(key: PreferenceKey<T>, default: T): Flow<T> =
        store.data.map { preferences -> preferences[key.key] ?: default }

    suspend fun <T> set(key: PreferenceKey<T>, value: T) =
        store.edit { preferences -> preferences[key.key] = value }

    override val locale: Flow<String> by lazy {
        get(PreferenceKey.StringKey(::locale.name), "uz")
    }

    override fun setLocale(value: String) {
        scope.launch {
            set(PreferenceKey.StringKey(::locale.name), value)
        }
    }

    override val tokenType: Flow<String> by lazy {
        get(PreferenceKey.StringKey(::tokenType.name), "")
    }

    override fun setTokenType(value: String) {
        scope.launch {
            set(PreferenceKey.StringKey(::tokenType.name), value)
        }
    }

    override val accessToken: Flow<String> by lazy {
        get(PreferenceKey.StringKey(::accessToken.name), "")
    }

    override fun setAccessToken(value: String) {
        scope.launch {
            set(PreferenceKey.StringKey(::accessToken.name), value)
        }
    }
}