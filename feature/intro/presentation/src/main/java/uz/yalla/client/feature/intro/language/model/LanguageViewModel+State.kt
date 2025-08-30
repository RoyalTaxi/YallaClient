package uz.yalla.client.feature.intro.language.model

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

internal fun LanguageViewModel.setLanguage(language: Language, context: Context) = intent {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        context.getSystemService(LocaleManager::class.java)
            ?.applicationLocales =
            LocaleList.forLanguageTags(language.languageTag)
    else
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(language.languageTag)
        )

    reduce {
        state.copy(selectedLanguage = language)
    }

    prefs.setLocale(language.languageTag)
}

