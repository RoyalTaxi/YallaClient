package uz.yalla.client.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import uz.yalla.client.core.data.local.AppPreferencesImpl
import uz.yalla.client.core.data.local.StaticPreferencesImpl
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.local.StaticPreferences
import java.io.File


private const val DATASTORE_FILE = "prefs.preferences_pb"

object Local {
    val module = module {

        single<DataStore<Preferences>> {
            PreferenceDataStoreFactory.createWithPath(
                produceFile = {
                    File(androidContext().filesDir, DATASTORE_FILE).absolutePath.toPath()
                }
            )
        }

        single<AppPreferences> {
            AppPreferencesImpl(get())
        }

        single<StaticPreferences> { StaticPreferencesImpl(androidContext()) }
    }
}