package uz.ildam.technologies.yalla.android.ui.screens.language

import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.launch
import uz.ildam.technologies.yalla.android.MainActivity
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.core.data.local.AppPreferences

internal data class Language(
    @StringRes val stringResId: Int,
    val languageTag: String,
)

@Composable
internal fun LanguageRoute(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val uz = "uz"
    val ru = "ru"
    val context = LocalContext.current as MainActivity
    var selectedLanguage by remember { mutableStateOf("") }
    val languages = remember {
        listOf(
            Language(
                stringResId = R.string.uzbek,
                languageTag = uz
            ),
            Language(
                stringResId = R.string.russian,
                languageTag = ru
            )
        )
    }

    LaunchedEffect(Unit) {
        launch {
            selectedLanguage =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    context
                        .getSystemService(LocaleManager::class.java)
                        .applicationLocales[0]
                        ?.toLanguageTag()
                        .orEmpty()
                else
                    AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag().orEmpty()
        }
    }

    LanguageScreen(
        selectedLanguage = selectedLanguage,
        languages = languages,
        onSelectLanguage = { lang ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                context.getSystemService(LocaleManager::class.java)
                    .applicationLocales = LocaleList.forLanguageTags(lang.languageTag)
            else
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(lang.languageTag)
                )
            context.recreate()
        },
        onBack = onBack,
        onNext = {
            AppPreferences.locale = selectedLanguage
            onNext()
        }
    )
}