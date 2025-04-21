package uz.yalla.client.core.data.local

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

sealed class PreferenceKey<T>(val key: Preferences.Key<T>) {
    class StringKey(name: String) : PreferenceKey<String>(stringPreferencesKey(name))
    class IntKey(name: String) : PreferenceKey<Int>(intPreferencesKey(name))
    class BooleanKey(name: String) : PreferenceKey<Boolean>(booleanPreferencesKey(name))
    class DoubleKey(name: String) : PreferenceKey<Double>(doublePreferencesKey(name))
    class LongKey(name: String) : PreferenceKey<Long>(longPreferencesKey(name))
    class FloatKey(name: String) : PreferenceKey<Float>(floatPreferencesKey(name))
}