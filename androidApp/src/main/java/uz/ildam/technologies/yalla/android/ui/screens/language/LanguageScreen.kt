package uz.ildam.technologies.yalla.android.ui.screens.language

import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import uz.ildam.technologies.yalla.android.MainActivity
import uz.ildam.technologies.yalla.android.R
import uz.ildam.technologies.yalla.android.components.button.YallaButton
import uz.ildam.technologies.yalla.android.components.item.LanguageItem
import uz.ildam.technologies.yalla.android.components.toolbar.YallaToolbar
import uz.ildam.technologies.yalla.android.design.theme.YallaTheme
import uz.ildam.technologies.yalla.android.ui.screens.login.LoginScreen
import uz.ildam.technologies.yalla.core.data.local.AppPreferences

class LanguageScreen : Screen {
    private val uz = "uz"
    private val ru = "ru"

    private data class Language(
        @StringRes val stringResId: Int,
        val languageTag: String,
    )

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current as MainActivity
        val selectedLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            context.getSystemService(LocaleManager::class.java).applicationLocales[0]
        else AppCompatDelegate.getApplicationLocales()[0]

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YallaTheme.color.white)
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            YallaToolbar(onClick = navigator::pop)

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(id = R.string.choose_language),
                color = YallaTheme.color.black,
                style = YallaTheme.font.headline,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(id = R.string.choose_language_desc),
                color = YallaTheme.color.gray,
                style = YallaTheme.font.body,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            languages.forEach { lang ->
                LanguageItem(
                    text = stringResource(id = lang.stringResId),
                    isSelected = selectedLanguage?.toLanguageTag() == lang.languageTag,
                    onSelect = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            context.getSystemService(LocaleManager::class.java)
                                .applicationLocales = LocaleList.forLanguageTags(lang.languageTag)
                        else AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(lang.languageTag)
                        )
                        context.recreate()
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            YallaButton(
                text = stringResource(id = R.string.next),
                onClick = {
                    AppPreferences.locale = selectedLanguage?.toLanguageTag() ?: "uz"
                    navigator push LoginScreen()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            )
        }
    }
}